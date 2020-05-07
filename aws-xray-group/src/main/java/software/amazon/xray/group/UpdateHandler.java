package software.amazon.xray.group;

import com.amazonaws.util.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.UpdateGroupRequest;
import software.amazon.awssdk.services.xray.model.UpdateGroupResponse;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.awssdk.services.xray.model.InsightsConfiguration;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    private static final String RESOURCE_NOT_FOUND_ERR_MESSAGE = "NOT FOUND";
    private XRayClient client;

    public UpdateHandler() {
        this.client = ClientBuilder.getClient();
    }

    @VisibleForTesting
    public UpdateHandler(XRayClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel requestResourceModel = request.getDesiredResourceState();

        ResourceModel responseResourceModel = doUpdateGroup(proxy, requestResourceModel, client, logger);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(responseResourceModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ResourceModel doUpdateGroup(AmazonWebServicesClientProxy proxy, ResourceModel model, XRayClient client,
                                          Logger logger) {
        UpdateGroupRequest.Builder updateGroupRequestBuilder = UpdateGroupRequest.builder();
        updateGroupRequestBuilder.filterExpression(model.getFilterExpression());
        UpdateGroupResponse updateGroupResponse;

        if (StringUtils.isNullOrEmpty(model.getGroupARN())) {
            updateGroupRequestBuilder.groupName(model.getGroupName());
        } else {
            updateGroupRequestBuilder.groupARN(model.getGroupARN());
        }

        if (model.getInsightsConfiguration() != null) {
            software.amazon.awssdk.services.xray.model.InsightsConfiguration.Builder insightsConfiguration =
                    InsightsConfiguration.builder();
            if (model.getInsightsConfiguration().getInsightsEnabled() != null) {
                insightsConfiguration.insightsEnabled(model.getInsightsConfiguration().getInsightsEnabled());
            }
            if (model.getInsightsConfiguration().getNotificationsEnabled() != null) {
                insightsConfiguration.notificationsEnabled(model.getInsightsConfiguration().getNotificationsEnabled());
            }
            updateGroupRequestBuilder.insightsConfiguration(insightsConfiguration.build());
        }

        UpdateGroupRequest updateGroupRequest = updateGroupRequestBuilder.build();

        try {
            updateGroupResponse = proxy.injectCredentialsAndInvokeV2(updateGroupRequest, client::updateGroup);
        } catch (InvalidRequestException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains(RESOURCE_NOT_FOUND_ERR_MESSAGE)) {
                throw new CfnNotFoundException(e);
            }
            throw new CfnInvalidRequestException(updateGroupRequest.toString(), e);
        }

        ResourceModel responseResourceModel = buildCreateGroupResourceResourceModel(updateGroupResponse);

        logger.log(String.format("%s [%s] updated successfully",
                ResourceModel.TYPE_NAME, responseResourceModel.getPrimaryIdentifier().toString()));

        return responseResourceModel;
    }

    private ResourceModel buildCreateGroupResourceResourceModel(UpdateGroupResponse updateGroupResponse) {
        ResourceModel.ResourceModelBuilder responseResourceModelBuilder = ResourceModel.builder();
        responseResourceModelBuilder.filterExpression(updateGroupResponse.group().filterExpression());
        responseResourceModelBuilder.groupARN(updateGroupResponse.group().groupARN());
        responseResourceModelBuilder.groupName(updateGroupResponse.group().groupName());
        responseResourceModelBuilder.insightsConfiguration(software.amazon.xray.group.InsightsConfiguration.builder()
                .insightsEnabled(updateGroupResponse.group().insightsConfiguration().insightsEnabled())
                .notificationsEnabled(updateGroupResponse.group().insightsConfiguration().notificationsEnabled())
                .build());

        return responseResourceModelBuilder.build();

    }
}
