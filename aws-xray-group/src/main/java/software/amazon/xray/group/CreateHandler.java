package software.amazon.xray.group;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.CreateGroupRequest;
import software.amazon.awssdk.services.xray.model.CreateGroupResponse;
import software.amazon.awssdk.services.xray.model.Tag;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.awssdk.services.xray.model.InsightsConfiguration;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateHandler extends BaseHandler<CallbackContext> {

    private static final String RESOURCE_ALREADY_EXISTS_ERR_MSG = "already exist";
    private XRayClient client;

    public CreateHandler() {
        this.client = ClientBuilder.getClient();
    }

    @VisibleForTesting
    public CreateHandler(XRayClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel requestResourceModel = request.getDesiredResourceState();

        ResourceModel responseResourceModel = doCreateGroup(proxy, requestResourceModel, client, logger);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(responseResourceModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ResourceModel doCreateGroup(AmazonWebServicesClientProxy proxy, ResourceModel model, XRayClient client,
                                          Logger logger) {

        List<Tag> tags = new ArrayList<>();
        CreateGroupRequest.Builder createGroupRequestBuilder = CreateGroupRequest.builder();
        createGroupRequestBuilder.groupName(model.getGroupName());
        createGroupRequestBuilder.filterExpression(model.getFilterExpression());

        if (model.getInsightsConfiguration() != null) {
            InsightsConfiguration.Builder insightsConfiguration = InsightsConfiguration.builder();
            if (model.getInsightsConfiguration().getInsightsEnabled() != null) {
                insightsConfiguration.insightsEnabled(model.getInsightsConfiguration().getInsightsEnabled());
            }
            if (model.getInsightsConfiguration().getNotificationsEnabled() != null) {
                insightsConfiguration.notificationsEnabled(model.getInsightsConfiguration().getNotificationsEnabled());
            }
            createGroupRequestBuilder.insightsConfiguration(insightsConfiguration.build());
        }

        if (model.getTags() != null) {
            for (Tags eachTag : model.getTags()) {
                Tag tag = Tag.builder().key(eachTag.getKey())
                                       .value(eachTag.getValue())
                                       .build();
                tags.add(tag);
            }
            createGroupRequestBuilder.tags(tags);
        }

        CreateGroupRequest createGroupRequest = createGroupRequestBuilder.build();
        CreateGroupResponse createGroupResponse;
        try {
            createGroupResponse = proxy.injectCredentialsAndInvokeV2(createGroupRequest, client::createGroup);
        } catch (InvalidRequestException e) {
            if (e.getMessage() != null && e.getMessage().contains(RESOURCE_ALREADY_EXISTS_ERR_MSG)) {
                throw new CfnAlreadyExistsException(e);
            }
            throw new CfnInvalidRequestException(
                    String.format("%s : %s", createGroupRequest.toString(), e.getMessage()),
                    e);
        }

        ResourceModel responseResourceModel = buildCreateGroupResponseResourceModel(createGroupResponse);

        logger.log(String.format("%s [%s] created successfully",
                ResourceModel.TYPE_NAME, responseResourceModel.getPrimaryIdentifier().toString()));

        return responseResourceModel;
    }

    private ResourceModel buildCreateGroupResponseResourceModel(CreateGroupResponse createGroupResponse) {
        ResourceModel.ResourceModelBuilder responseResourceModelBuilder = ResourceModel.builder();
        responseResourceModelBuilder.filterExpression(createGroupResponse.group().filterExpression());
        responseResourceModelBuilder.groupARN(createGroupResponse.group().groupARN());
        responseResourceModelBuilder.groupName(createGroupResponse.group().groupName());
        responseResourceModelBuilder.insightsConfiguration(software.amazon.xray.group.InsightsConfiguration.builder()
                .insightsEnabled(createGroupResponse.group().insightsConfiguration().insightsEnabled())
                .notificationsEnabled(createGroupResponse.group().insightsConfiguration().notificationsEnabled())
                .build());

        return responseResourceModelBuilder.build();

    }
}
