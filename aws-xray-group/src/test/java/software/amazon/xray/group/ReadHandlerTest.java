package software.amazon.xray.group;

import com.amazonaws.AmazonServiceException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.Group;
import software.amazon.awssdk.services.xray.model.GetGroupRequest;
import software.amazon.awssdk.services.xray.model.GetGroupResponse;
import software.amazon.awssdk.services.xray.model.InsightsConfiguration;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;
    private XRayClient client;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        client = ClientBuilderTestHelper.getClient();
    }

    @Test
    public void handleRequestSuccessScenario() {
        final ReadHandler handler = new ReadHandler(client);

        final ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder();
        modelBuilder.groupName("test-group");
        ResourceModel model = modelBuilder.build();

        Group.Builder groupBuilder = Group.builder();
        groupBuilder.groupName("test-group");
        groupBuilder.filterExpression("test-filter");
        groupBuilder.groupARN("test-arn");
        groupBuilder.insightsConfiguration(InsightsConfiguration.builder().insightsEnabled(false).notificationsEnabled(false).build());
        GetGroupResponse getGroupResponse = GetGroupResponse.builder().group(groupBuilder.build()).build();
        doReturn(getGroupResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        any(),
                        any()
                );

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null,
                logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo((OperationStatus.SUCCESS));
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getResourceModel().getGroupName()).isEqualTo("test-group");
        assertThat(response.getResourceModel().getGroupARN()).isEqualTo("test-arn");
        assertThat(response.getResourceModel().getFilterExpression()).isEqualTo("test-filter");
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequestUnknownError() {
        final ReadHandler handler = new ReadHandler(client);

        doThrow(SdkException.builder().message("test error").build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        any(),
                        any()
                );

        final ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder();
        modelBuilder.groupName("test-group");
        ResourceModel model = modelBuilder.build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(SdkException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }

    @Test
    public void handleRequestAmazonServiceException() {
        final ReadHandler handler = new ReadHandler(client);

        doThrow(new AmazonServiceException("test error"))
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        any(),
                        any()
                );

        final ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder();
        modelBuilder.groupName("test-group");
        ResourceModel model = modelBuilder.build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(AmazonServiceException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }


    @Test
    public void handleRequestNotFoundException() {
        final ReadHandler handler = new ReadHandler(client);

        doThrow(InvalidRequestException.builder().message("Group not found").build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(GetGroupRequest.class), any());

        final ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder();
        modelBuilder.groupARN("test-group-arn");
        ResourceModel model = modelBuilder.build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }
}
