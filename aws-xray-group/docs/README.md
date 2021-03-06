# AWS::XRay::Group

This schema provides construct and validation rules for AWS-XRay Group resource parameters.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::XRay::Group",
    "Properties" : {
        "<a href="#filterexpression" title="FilterExpression">FilterExpression</a>" : <i>String</i>,
        "<a href="#groupname" title="GroupName">GroupName</a>" : <i>String</i>,
        "<a href="#insightsconfiguration" title="InsightsConfiguration">InsightsConfiguration</a>" : <i><a href="insightsconfiguration.md">InsightsConfiguration</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ [ <a href="tags.md">Tags</a>, ... ], ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::XRay::Group
Properties:
    <a href="#filterexpression" title="FilterExpression">FilterExpression</a>: <i>String</i>
    <a href="#groupname" title="GroupName">GroupName</a>: <i>String</i>
    <a href="#insightsconfiguration" title="InsightsConfiguration">InsightsConfiguration</a>: <i><a href="insightsconfiguration.md">InsightsConfiguration</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - 
      - <a href="tags.md">Tags</a></i>
</pre>

## Properties

#### FilterExpression

The filter expression defining criteria by which to group traces.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### GroupName

The case-sensitive name of the new group. Names must be unique.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>32</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InsightsConfiguration

_Required_: No

_Type_: <a href="insightsconfiguration.md">InsightsConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of List of <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the GroupARN.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### GroupARN

The ARN of the group that was generated on creation.

