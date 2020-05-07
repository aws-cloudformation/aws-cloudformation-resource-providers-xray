package software.amazon.xray.group;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetGroupRequest;
import software.amazon.awssdk.services.xray.model.GetGroupResponse;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandler<CallbackContext> {

    private static final String RESOURCE_NOT_FOUND_ERR_MESSAGE = "NOT FOUND";
    private XRayClient client;

    public ReadHandler() {
        this.client = ClientBuilder.getClient();
    }

    @VisibleForTesting
    public ReadHandler(XRayClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel requestResourceModel = request.getDesiredResourceState();
        final ResourceModel responseResourceModel;

        responseResourceModel = doGetGroup(proxy, requestResourceModel, client, logger);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(responseResourceModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ResourceModel doGetGroup(AmazonWebServicesClientProxy proxy, ResourceModel model, XRayClient client,
                                       Logger logger) {
        GetGroupRequest.Builder getGroupRequestBuilder = GetGroupRequest.builder();
        getGroupRequestBuilder.groupARN(model.getGroupARN());
        final GetGroupRequest getGroupRequest = getGroupRequestBuilder.build();
        final GetGroupResponse getGroupResponse;

        try {
            getGroupResponse = proxy.injectCredentialsAndInvokeV2(getGroupRequest, client::getGroup);
        } catch (InvalidRequestException e) {
            throw new CfnNotFoundException(e);
        }

        ResourceModel responseResourceModel = buildGetGroupResourceResourceModel(getGroupResponse);

        logger.log(String.format("%s [%s] read group successfully",
                ResourceModel.TYPE_NAME, responseResourceModel.getPrimaryIdentifier().toString()));

        return responseResourceModel;
    }

    private ResourceModel buildGetGroupResourceResourceModel(GetGroupResponse getGroupResponse) {
        ResourceModel.ResourceModelBuilder responseResourceModelBuilder = ResourceModel.builder();
        responseResourceModelBuilder.filterExpression(getGroupResponse.group().filterExpression());
        responseResourceModelBuilder.groupARN(getGroupResponse.group().groupARN());
        responseResourceModelBuilder.groupName(getGroupResponse.group().groupName());
        responseResourceModelBuilder.insightsConfiguration(InsightsConfiguration.builder()
                .insightsEnabled(getGroupResponse.group().insightsConfiguration().insightsEnabled())
                .notificationsEnabled(getGroupResponse.group().insightsConfiguration().notificationsEnabled())
                .build());

        return responseResourceModelBuilder.build();

    }
}
