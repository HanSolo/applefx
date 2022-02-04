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
- MacosWindowButton
- MacosWindow

The MacosWindow autodetects the current appearance (dark/bright mode and accent color) and
switches all MacosControls that it contains to the new values.
Because the WatcherService needs some time to detect the switch between modes it can
take up to 5-10 seconds before the app will change it's appearance.


## Macos Light Mode
![BrightMode](https://i.ibb.co/xLjNxNb/Apple-FX-light.png)


## Macos Dark Mode
![DarkMode](https://i.ibb.co/bbTmsW3/Apple-FX-dark.png)