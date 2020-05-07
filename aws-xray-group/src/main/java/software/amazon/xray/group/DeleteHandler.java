package software.amazon.xray.group;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.DeleteGroupRequest;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    private static final String RESOURCE_NOT_FOUND_ERR_MESSAGE = "NOT FOUND";
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

        return doDeleteGroup(proxy, requestResourceModel, client, logger);
    }

    private ProgressEvent<ResourceModel, CallbackContext> doDeleteGroup(AmazonWebServicesClientProxy proxy, ResourceModel model, XRayClient client,
                               Logger logger) {
        DeleteGroupRequest.Builder deleteGroupRequestBuilder = DeleteGroupRequest.builder();
        deleteGroupRequestBuilder.groupARN(model.getGroupARN());
        DeleteGroupRequest deleteGroupRequest = deleteGroupRequestBuilder.build();

        try {
            proxy.injectCredentialsAndInvokeV2(deleteGroupRequest, client::deleteGroup);
        } catch (InvalidRequestException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains(RESOURCE_NOT_FOUND_ERR_MESSAGE)) {
                return  ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .status(OperationStatus.FAILED)
                                .errorCode(HandlerErrorCode.NotFound)
                                .build();
            } else {
                return  ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .status(OperationStatus.FAILED)
                                .errorCode(HandlerErrorCode.InvalidRequest)
                                .build();
            }
        } catch (Exception e) {
            logger.log(String.format("%s [%s] Exception detected. message: [%s]",
                    ResourceModel.TYPE_NAME,
                    model.getGroupARN(),
                    e.getMessage()));
            return  ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.InternalFailure)
                            .build();
        }

        logger.log(String.format("%s [%s] deleted successfully",
                ResourceModel.TYPE_NAME,
                model.getGroupARN()));

        return  ProgressEvent.<ResourceModel, CallbackContext>builder()
                              .status(OperationStatus.SUCCESS)
                              .build();
    }
}
