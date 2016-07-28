package yio.tro.evolution;

/**
 * Created by ivan on 22.07.14.
 */
public class ButtonFactory {
    MenuController menuController;
    ButtonRenderer buttonRenderer;

    public ButtonFactory(MenuController menuController) {
        this.menuController = menuController;
        buttonRenderer = new ButtonRenderer();
    }

    public Button getButton(SimpleRectangle position, int id, String text) {
        Button button = menuController.getButtonById(id);
        if (button == null) { // if it's the first time
            button = new Button(position, id, menuController);
            if (text != null) {
                button.addTextLine(text);
                buttonRenderer.renderButton(button);
            }
            menuController.addButtonToArray(button);
        }
        button.setVisible(true);
        button.setTouchable(true);
        button.factorModel.beginSpawning(1, 1.5);
        button.factorModel.setValues(0, 0.001);
//        buttonLighty.touchAnimation = true;
        return button;
    }
}
