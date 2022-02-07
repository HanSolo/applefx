## Apple FX
A collection of Apple UI controls implemented in JavaFX.

Available Macos controls:
- MacosButton
- MacosCheckBox
- MacosRadioButton
- MacosLabel
- MacosComboBox
- MacosLabel
- MacosSeparator
- MacosScrollPane
- MacosSlider
- MacosSwitch
- MacosAddRemoveButton
- MacosToggleButtonBar
- MacosToggleButton
- MacosTextField
- MacosProgress
- MacosWindowButton
- MacosWindow

The MacosWindow autodetects the current appearance (dark/bright mode and accent color) and
switches all MacosControls that it contains to the new values.
Because the WatcherService needs some time to detect the switch between modes it can
take up to 5-10 seconds before the app will change it's appearance.

There are also 1227 SF Symbol monochrome symbols that you can use. Please find all available
icons with their names [here](https://framework7.io/icons/).

You can use these symbols as follows:

```java
MacosLabel symbol = new MacosLabel(SFIcon.camera.utf8());
symbol.setFont(Fonts.sfIconSets(32));
```
<b>IMPORTANT</b>
All SF Symbols shall be considered to be system-provided images as defined in the Xcode and 
Apple SDKs license agreements and are subject to the terms and conditions set forth therein. 
You may not use SF Symbols—or glyphs that are substantially or confusingly similar—in your app icons, 
logos, or any other trademark-related use. Apple reserves the right to review and, in its sole discretion, 
require modification or discontinuance of use of any Symbol used in violation of the foregoing restrictions, 
and you agree to promptly comply with any such request.

## Macos Light Mode
![BrightMode](https://i.ibb.co/xLjNxNb/Apple-FX-light.png)


## Macos Dark Mode
![DarkMode](https://i.ibb.co/bbTmsW3/Apple-FX-dark.png)