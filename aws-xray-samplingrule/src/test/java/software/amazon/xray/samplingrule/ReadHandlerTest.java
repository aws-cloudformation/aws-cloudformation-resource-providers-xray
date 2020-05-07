package software.amazon.xray.samplingrule;

import com.amazonaws.AmazonServiceException;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetSamplingRulesResponse;
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
import software.amazon.xray.samplingrule.utils.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ReadHandler handler;

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
        modelBuilder.ruleARN("test-ruleARN");
        ResourceModel model = modelBuilder.build();

        GetSamplingRulesResponse getSamplingRulesResponse = buildMockGetSamplingRulesResponse();
        doReturn(getSamplingRulesResponse)
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
        assertThat(response
                .getResourceModel()
                .getSamplingRule()
                .getRuleName()).isEqualTo("test-ruleName");
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }


    @Test
    public void handleRequestAmazonServiceException() {
        doThrow(new AmazonServiceException("test error"))
                .when(handler)
                .handleRequest(
                        any(), any(), any(), any()
                );

        final ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder();
        modelBuilder.ruleARN("test-ruleARN");
        ResourceModel model = modelBuilder.build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(AmazonServiceException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }

    @Test
    public void handleRequestCfnNotFoundException() {
        doThrow(CfnNotFoundException.class)
                .when(handler)
                .handleRequest(
                        any(), any(), any(), any()
                );
        final ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder();
        modelBuilder.ruleARN("test-ruleARN");
        ResourceModel model = modelBuilder.build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }

    private  GetSamplingRulesResponse buildMockGetSamplingRulesResponse() {
        GetSamplingRulesResponse.Builder getSamplingRulesResponseBuilder = GetSamplingRulesResponse.builder();
        getSamplingRulesResponseBuilder.samplingRuleRecords(TestUtils.buildMockSamplingRuleRecord());

        return getSamplingRulesResponseBuilder.build();
    }
}
