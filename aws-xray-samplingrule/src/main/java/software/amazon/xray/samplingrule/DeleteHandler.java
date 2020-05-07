package software.amazon.xray.samplingrule;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.DeleteSamplingRuleRequest;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    private static final String RESOURCE_NOT_FOUND_ERR_MESSAGE = "DOES NOT EXIST";
    private XRayClient client;

    public DeleteHandler() {
        this.client = ClientBuilder.getClient();
    }

    @VisibleForTesting
    public DeleteHandler(XRayClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel requestResourceModel = request.getDesiredResourceState();

        doDeleteSamplingRule(proxy, requestResourceModel, client);

        logger.log(String.format("%s [%s] delete successfully",
                ResourceModel.TYPE_NAME, requestResourceModel.getPrimaryIdentifier().toString()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(null)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private void doDeleteSamplingRule(AmazonWebServicesClientProxy proxy, ResourceModel model,
                                               XRayClient client) {
        DeleteSamplingRuleRequest.Builder deleteSamplingRuleRequestBuilder = DeleteSamplingRuleRequest.builder();
        deleteSamplingRuleRequestBuilder.ruleARN(model.getRuleARN());
        DeleteSamplingRuleRequest deleteSamplingRuleRequest = deleteSamplingRuleRequestBuilder.build();

        try {
            proxy.injectCredentialsAndInvokeV2(deleteSamplingRuleRequest,
                    client::deleteSamplingRule);
        } catch (InvalidRequestException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains(RESOURCE_NOT_FOUND_ERR_MESSAGE)) {
                throw new CfnNotFoundException(e);
            } else {
                throw new CfnInvalidRequestException(deleteSamplingRuleRequest.toString(), e);
            }
        }
    }
}
