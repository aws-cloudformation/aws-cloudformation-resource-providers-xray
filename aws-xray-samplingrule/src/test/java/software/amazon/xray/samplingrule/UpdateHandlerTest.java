package software.amazon.xray.samplingrule;

import com.amazonaws.AmazonServiceException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.UpdateSamplingRuleRequest;
import software.amazon.awssdk.services.xray.model.UpdateSamplingRuleResponse;
import software.amazon.awssdk.services.xray.model.SamplingRuleRecord;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.awssdk.utils.ImmutableMap;
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
import static software.amazon.xray.samplingrule.utils.TestUtils.buildMockSamplingRuleRecord;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

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
        final UpdateHandler handler = new UpdateHandler(client);

        final ResourceModel model = buildMockUpdateSamplingRuleRequestResourceModel();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        UpdateSamplingRuleResponse updateSamplingRuleResponse = buildMockUpdateSamplingRuleResponse();
        doReturn(updateSamplingRuleResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        any(),
                        any()
                );
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null,
                logger);

        SamplingRule samplingRuleResponse = response.getResourceModel().getSamplingRule();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo((OperationStatus.SUCCESS));
        assertThat(response.getCallbackContext()).isNull();
        assertThat(samplingRuleResponse.getHost()).isEqualTo("test-host");
        assertThat(samplingRuleResponse.getHTTPMethod()).isEqualTo("test-httpMethod");
        assertThat(samplingRuleResponse.getPriority()).isEqualTo(1);
        assertThat(samplingRuleResponse.getResourceARN()).isEqualTo("test-resource-arn");
        assertThat(samplingRuleResponse.getReservoirSize()).isEqualTo(50);
        assertThat(samplingRuleResponse.getServiceType()).isEqualTo("test-service-type");
        assertThat(samplingRuleResponse.getServiceName()).isEqualTo("test-service-name");
        assertThat(samplingRuleResponse.getURLPath()).isEqualTo("test-url");
        assertThat(samplingRuleResponse.getVersion()).isEqualTo(1);
        assertThat(samplingRuleResponse.getRuleARN()).isEqualTo("test-ruleARN");
        assertThat(samplingRuleResponse.getRuleName()).isEqualTo("test-ruleName");
        assertThat(samplingRuleResponse.getFixedRate()).isEqualTo(0.1);
        assertThat(samplingRuleResponse.getAttributes()).isEqualTo(ImmutableMap.of("key", "value"));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequestUnknownError() {
        final UpdateHandler handler = new UpdateHandler(client);

        doThrow(SdkException.builder().message("test error").build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        any(),
                        any()
                );

        final ResourceModel model = buildMockUpdateSamplingRuleRequestResourceModel();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(SdkException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }

    @Test
    public void handleRequestAmazonServiceException() {
        final UpdateHandler handler = new UpdateHandler(client);

        doThrow(new AmazonServiceException("test error"))
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        any(),
                        any()
                );

        final ResourceModel model = buildMockUpdateSamplingRuleRequestResourceModel();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(AmazonServiceException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }

    @Test
    public void handleRequestGroupResourceNotFound() {
        final UpdateHandler handler = new UpdateHandler(client);

        doThrow(InvalidRequestException.builder().message("does not " +
                "exists").build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(UpdateSamplingRuleRequest.class), any());

        final ResourceModel model = buildMockUpdateSamplingRuleRequestResourceModel();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }

    @Test
    public void handleRequestInvalidRequestException() {
        final UpdateHandler handler = new UpdateHandler(client);

        doThrow(InvalidRequestException.class).when(proxy)
                .injectCredentialsAndInvokeV2(any(UpdateSamplingRuleRequest.class), any());

        final ResourceModel model = buildMockUpdateSamplingRuleRequestResourceModel();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, null, logger);
        });
    }

    private ResourceModel buildMockUpdateSamplingRuleRequestResourceModel() {
        ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder();
        SamplingRule.SamplingRuleBuilder samplingRuleBuilder =
                software.amazon.xray.samplingrule.SamplingRule.builder();
        samplingRuleBuilder.host("test-host");
        samplingRuleBuilder.hTTPMethod("test-httpMethod");
        samplingRuleBuilder.priority(1);
        samplingRuleBuilder.resourceARN("test-resource-arn");
        samplingRuleBuilder.reservoirSize(50);
        samplingRuleBuilder.serviceType("test-service-type");
        samplingRuleBuilder.serviceName("test-service-name");
        samplingRuleBuilder.uRLPath("test-url");
        samplingRuleBuilder.ruleARN("test-ruleARN");
        samplingRuleBuilder.ruleName("test-ruleName");
        samplingRuleBuilder.fixedRate(0.1);
        samplingRuleBuilder.attributes(ImmutableMap.of("key", "value"));

        modelBuilder.samplingRule(samplingRuleBuilder.build());
        return modelBuilder.build();
    }


    private UpdateSamplingRuleResponse buildMockUpdateSamplingRuleResponse() {
        UpdateSamplingRuleResponse.Builder updateSamplingRuleResponseBuilder = UpdateSamplingRuleResponse.builder();
        SamplingRuleRecord samplingRuleRecord = buildMockSamplingRuleRecord();

        return updateSamplingRuleResponseBuilder
                .samplingRuleRecord(samplingRuleRecord)
                .build();
    }
}
