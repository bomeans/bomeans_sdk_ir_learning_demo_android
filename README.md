# bomeans_sdk_ir_learning_demo_android
Demonstrate how to using the IR learning and remote controller matching service of the Bomeans IR Cloud service via the associated SDK.

General IR learning just acts like a signal recorder. Bomeans SDK alone with the cloud service provides more than this.

The learned signal (recorded IR waveform) can be processed and analyzed by the IR Reader Engine in the SDK to extract the characteristics of the IR signals. The characteristics include the IR format, custom code, and the key code, etc. These characteristics can then send to the cloud to match the existing remote controllers in the database.

This unique feature can be treated as an easy way to pick up the suitable remote controller from the IR database which may contain thousands of entries. Typically the user have to go through a testing process by pressing the test button many times in order to test if the selected remote controller is the right one.

The whole process may look like this:
(1) User aim his remote controller to the IR receiver.
(2) Press arbitrary keys on the remote controller.
(3) Matched remote controller(s) is sent back to user.

And, yes, there might still have multiple remote controllers are matched. Most likely this is because some remote controllers share the same keys but have some advanced or special function keys that are different. In this case, the App can just recommend the one with more keys.

[Note] You need to apply an Bomeans IR API Key for this demo code to run.
