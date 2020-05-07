# AWS::XRay::SamplingRule

This schema provides construct and validation rules for AWS-XRay SamplingRule resource parameters.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::XRay::SamplingRule",
    "Properties" : {
        "<a href="#samplingrule" title="SamplingRule">SamplingRule</a>" : <i><a href="samplingrule.md">SamplingRule</a></i>,
        "<a href="#samplingrulerecord" title="SamplingRuleRecord">SamplingRuleRecord</a>" : <i><a href="samplingrulerecord.md">SamplingRuleRecord</a></i>,
        "<a href="#samplingruleupdate" title="SamplingRuleUpdate">SamplingRuleUpdate</a>" : <i><a href="samplingruleupdate.md">SamplingRuleUpdate</a></i>,
        "<a href="#rulename" title="RuleName">RuleName</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ [ <a href="tags.md">Tags</a>, ... ], ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::XRay::SamplingRule
Properties:
    <a href="#samplingrule" title="SamplingRule">SamplingRule</a>: <i><a href="samplingrule.md">SamplingRule</a></i>
    <a href="#samplingrulerecord" title="SamplingRuleRecord">SamplingRuleRecord</a>: <i><a href="samplingrulerecord.md">SamplingRuleRecord</a></i>
    <a href="#samplingruleupdate" title="SamplingRuleUpdate">SamplingRuleUpdate</a>: <i><a href="samplingruleupdate.md">SamplingRuleUpdate</a></i>
    <a href="#rulename" title="RuleName">RuleName</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - 
      - <a href="tags.md">Tags</a></i>
</pre>

## Properties

#### SamplingRule

_Required_: No

_Type_: <a href="samplingrule.md">SamplingRule</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SamplingRuleRecord

_Required_: No

_Type_: <a href="samplingrulerecord.md">SamplingRuleRecord</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SamplingRuleUpdate

_Required_: No

_Type_: <a href="samplingruleupdate.md">SamplingRuleUpdate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RuleName

The ARN of the sampling rule. Specify a rule by either name or ARN, but not both.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>32</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of List of <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the RuleARN.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### RuleARN

The ARN of the sampling rule. Specify a rule by either name or ARN, but not both.

