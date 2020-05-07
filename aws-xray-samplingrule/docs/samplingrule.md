# AWS::XRay::SamplingRule SamplingRule

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#attributes" title="Attributes">Attributes</a>" : <i><a href="samplingrule-attributes.md">Attributes</a></i>,
    "<a href="#fixedrate" title="FixedRate">FixedRate</a>" : <i>Double</i>,
    "<a href="#host" title="Host">Host</a>" : <i>String</i>,
    "<a href="#httpmethod" title="HTTPMethod">HTTPMethod</a>" : <i>String</i>,
    "<a href="#priority" title="Priority">Priority</a>" : <i>Integer</i>,
    "<a href="#reservoirsize" title="ReservoirSize">ReservoirSize</a>" : <i>Integer</i>,
    "<a href="#resourcearn" title="ResourceARN">ResourceARN</a>" : <i>String</i>,
    "<a href="#rulearn" title="RuleARN">RuleARN</a>" : <i>String</i>,
    "<a href="#rulename" title="RuleName">RuleName</a>" : <i>String</i>,
    "<a href="#servicename" title="ServiceName">ServiceName</a>" : <i>String</i>,
    "<a href="#servicetype" title="ServiceType">ServiceType</a>" : <i>String</i>,
    "<a href="#urlpath" title="URLPath">URLPath</a>" : <i>String</i>,
    "<a href="#version" title="Version">Version</a>" : <i>Integer</i>
}
</pre>

### YAML

<pre>
<a href="#attributes" title="Attributes">Attributes</a>: <i><a href="samplingrule-attributes.md">Attributes</a></i>
<a href="#fixedrate" title="FixedRate">FixedRate</a>: <i>Double</i>
<a href="#host" title="Host">Host</a>: <i>String</i>
<a href="#httpmethod" title="HTTPMethod">HTTPMethod</a>: <i>String</i>
<a href="#priority" title="Priority">Priority</a>: <i>Integer</i>
<a href="#reservoirsize" title="ReservoirSize">ReservoirSize</a>: <i>Integer</i>
<a href="#resourcearn" title="ResourceARN">ResourceARN</a>: <i>String</i>
<a href="#rulearn" title="RuleARN">RuleARN</a>: <i>String</i>
<a href="#rulename" title="RuleName">RuleName</a>: <i>String</i>
<a href="#servicename" title="ServiceName">ServiceName</a>: <i>String</i>
<a href="#servicetype" title="ServiceType">ServiceType</a>: <i>String</i>
<a href="#urlpath" title="URLPath">URLPath</a>: <i>String</i>
<a href="#version" title="Version">Version</a>: <i>Integer</i>
</pre>

## Properties

#### Attributes

Matches attributes derived from the request.

_Required_: No

_Type_: <a href="samplingrule-attributes.md">Attributes</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### FixedRate

The percentage of matching requests to instrument, after the reservoir is exhausted.

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Host

Matches the hostname from a request URL.

_Required_: No

_Type_: String

_Maximum_: <code>64</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### HTTPMethod

Matches the HTTP method from a request URL.

_Required_: No

_Type_: String

_Maximum_: <code>10</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Priority

The priority of the sampling rule.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ReservoirSize

A fixed number of matching requests to instrument per second, prior to applying the fixed rate. The reservoir is not used directly by services, but applies to all services using the rule collectively.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ResourceARN

Matches the ARN of the AWS resource on which the service runs.

_Required_: No

_Type_: String

_Maximum_: <code>500</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RuleARN

The ARN of the sampling rule. Specify a rule by either name or ARN, but not both.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RuleName

The ARN of the sampling rule. Specify a rule by either name or ARN, but not both.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>32</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ServiceName

Matches the name that the service uses to identify itself in segments.

_Required_: No

_Type_: String

_Maximum_: <code>64</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ServiceType

Matches the origin that the service uses to identify its type in segments.

_Required_: No

_Type_: String

_Maximum_: <code>64</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### URLPath

Matches the path from a request URL.

_Required_: No

_Type_: String

_Maximum_: <code>128</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Version

The version of the sampling rule format (1)

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

