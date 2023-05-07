package com.example.keyvault_client.viewControllers;
import com.example.keyvault_client.NodeGenerator;
import com.example.keyvault_client.ViewManager;
import com.keyvault.database.models.Devices;
import com.keyvault.database.models.Items;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainController {

    @FXML
    public VBox scrollItemContainer, messageContainer;
    @FXML
    public Label userNameLabel, allButton, menuFavorites, menuPasswords, menuNotes, menuSettings, closeSession, categoriesTitle;
    @FXML
    public TextField searchField;
    @FXML
    public StackPane mainBody;
    @FXML
    public HBox topMenuContainer,infoContainer;
    public Label selectedMenu, messageLabel, devicesLabel;
    private List<Items> userItems = new ArrayList<>();
    private List<Items> userFavorites = new ArrayList<>();
    private List<Devices> userDevices = new ArrayList<>();
    private final Image fillStar = new Image(ViewManager.class.getResourceAsStream("icons/starFill.png"));
    private final Image emptyStar = new Image(ViewManager.class.getResourceAsStream("icons/star.png"));
    private Items selectedItem = null;
    private CreateUpdateController createUpdateController = null;
    private ExecutorService executorService;

    public void initialize()
    {
        executorService = ViewManager.executorService;
        userNameLabel.setText(ViewManager.conn.getEmail());
        scrollItemContainer.setAlignment(Pos.CENTER);
        scrollItemContainer.getChildren().add(new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS));
        executorService.execute(this::getContent);
        executorService.execute(this::getDevices);
        selectedMenu = allButton;
    }

    @FXML
    public void actionMenu(MouseEvent e)
    {
        Label target = (Label) e.getSource();
        changeMenu(target);
    }

    private void changeMenu(Label target)
    {
        if(!userItems.isEmpty())
        {
            selectedMenu.getStyleClass().remove("menuButtonSelected");
            target.getStyleClass().add("menuButtonSelected");
            selectedMenu = target;

            scrollItemContainer.getChildren().clear();

            switch (target.getId())
            {
                case "all" -> getAllContent();
                case "favorites" -> getFavorites();
                case "notes" -> getNotes();
                case "passwords" -> getPasswords();
            }
        }
    }

    @FXML
    public void closeSession(MouseEvent event){
        ViewManager.conn.closeSession(true);
    }

    @FXML
    public void searchItem()
    {
        String toSearch = searchField.getText().trim().toLowerCase();
        System.out.println(toSearch);

        if(!toSearch.isBlank() && !toSearch.isEmpty())
        {
            scrollItemContainer.getChildren().clear();

            for (Items i : userItems) {
                if (i.getName().contains(toSearch))
                {
                   HBox card = generateItemCard(i);
                   scrollItemContainer.getChildren().add(card);
                }
            }
        }
        else
        {
            changeMenu(selectedMenu);
        }
    }
    public void changeLanguage()
    {
        allButton.setText(ViewManager.bundle.getString("allItems"));
        menuFavorites.setText(ViewManager.bundle.getString("favorites"));
        searchField.setPromptText(ViewManager.bundle.getString("search"));
        categoriesTitle.setText(ViewManager.bundle.getString("categories"));
        menuPasswords.setText(ViewManager.bundle.getString("passwords"));
        menuNotes.setText(ViewManager.bundle.getString("notes"));
        menuSettings.setText(ViewManager.bundle.getString("configuration"));
        closeSession.setText(ViewManager.bundle.getString("closeSession"));
    }

    @FXML
    public void changeUserImage()
    {
        FileChooser fileChooser = new FileChooser();
        File file = null;
        fileChooser.setTitle("Imagen de perfil");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (*.png , *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));
        file = fileChooser.showOpenDialog(ViewManager.getStage());
    }

    @FXML
    public void showCreateView(){
        addViewToInfoContainer("create-update-view.fxml", false);
    }

    public void showEditView(){
        addViewToInfoContainer("create-update-view.fxml", true);
    }

    public void showMessage(String text, boolean isError)
    {
        FadeTransition fade = new FadeTransition(Duration.millis(200), messageContainer);

        if(isError)
        {
            messageContainer.getStyleClass().add("messageError");
        }
        else
        {
            messageContainer.getStyleClass().remove("messageError");
        }

        messageContainer.setVisible(true);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
        messageLabel.setText(text);

        new Thread(() ->{
            try {
                Thread.sleep(3000);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(e -> messageContainer.setVisible(false));
                fade.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void showItemInfo(Items target)
    {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("views/item-view.fxml"));
            AnchorPane box = loader.load();

            ShowController controller = loader.getController();
            controller.initialize(target, topMenuContainer);

            if(infoContainer.getChildren().size() == 1)
                infoContainer.getChildren().clear();

            infoContainer.getChildren().add(box);

            selectedItem = target;
            generateActionButtons();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addViewToInfoContainer(String fxml, boolean isEdit)
    {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("views/" + fxml));
            AnchorPane box = loader.load();

            createUpdateController = loader.getController();
            createUpdateController.initialize(this);

            if(isEdit)
                createUpdateController.enableEditMode(selectedItem);

            if(infoContainer.getChildren().size() == 1)
                infoContainer.getChildren().clear();

            infoContainer.getChildren().add(box);
            generateCreateActionButtons(isEdit);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HBox generateItemCard(Items item)
    {
        HBox container = NodeGenerator.generateItemCard(item);
        container.setOnMouseClicked((e) -> showItemInfo(item));

        return container;
    }

    private void generateActionButtons()
    {
        Button editButton = NodeGenerator.generateActionButton("pencil.png", null, "actionButton");
        Button deleteButton = NodeGenerator.generateActionButton("trash.png", null, "actionButton");
        Button favButton = NodeGenerator.generateActionButton(selectedItem.getFav() ? "starFill.png" : "star.png", null, "actionButton");
        Button reloadButton = NodeGenerator.generateActionButton("reloadWhite.png", null, "actionButton");

        deleteButton.setOnMouseClicked((e) -> executorService.execute(this::deleteItem));
        favButton.setOnMouseClicked((e) -> changeFav(favButton));
        editButton.setOnMouseClicked((e) -> showEditView());
        addButtonsTopMenu(Pos.CENTER_RIGHT, editButton, deleteButton, favButton, reloadButton);
    }

    private void generateCreateActionButtons(boolean isEdit)
    {
        Button cancel = NodeGenerator.generateActionButton("x.png", "   Cancelar", "actionButton");
        Button accept = NodeGenerator.generateActionButton("tick.png", "   Aceptar", "actionButton");

        accept.getStyleClass().add("actionButton");
        cancel.getStyleClass().add("actionButton");

        accept.setOnMouseClicked((e) -> executorService.execute(isEdit ? this::updateItem : this::insertItem));
        cancel.setOnMouseClicked((e) -> {
            Platform.runLater(() -> infoContainer.getChildren().clear());
            topMenuContainer.getChildren().clear();
        });
        addButtonsTopMenu(Pos.CENTER, cancel, accept);
    }

    private void addButtonsTopMenu(Pos pos, Button... buttons)
    {
        topMenuContainer.getChildren().clear();
        topMenuContainer.getChildren().addAll(buttons);
        topMenuContainer.setAlignment(pos);
    }

    private void insertItem()
    {
        Items newItem = createUpdateController.generateItem();
        executorService.execute(() -> ViewManager.conn.insertItem(newItem, this));
    }

    private void deleteItem(){
        executorService.execute(() -> ViewManager.conn.deleteItem(selectedItem, this));
    }

    private void updateItem()
    {
        Items modItem = createUpdateController.updateItem();
        executorService.execute(() -> ViewManager.conn.modItem(modItem, this));
    }

    public void reloadView()
    {
        selectedItem = null;
        infoContainer.getChildren().clear();
        topMenuContainer.getChildren().clear();
        scrollItemContainer.getChildren().clear();

        switch (selectedMenu.getId()) {
            case "all" -> getAllContent();
            case "favorites" -> getFavorites();
            case "notes" -> getNotes();
            case "passwords" -> getPasswords();
        }
    }

    private void changeFav(Button favButton)
    {
        selectedItem.setFav(!selectedItem.getFav());

        favButton.setGraphic(new ImageView(selectedItem.getFav() ? fillStar : emptyStar));

        if(selectedItem.getFav())
        {
            userFavorites.add(selectedItem);
        }
        else
        {
            userFavorites.remove(selectedItem);
        }

        executorService.execute(() -> ViewManager.conn.changeFav(selectedItem, this));
    }

    private void getDevices()
    {
        userDevices = ViewManager.conn.getDevices();
        Platform.runLater(() -> devicesLabel.setText(userDevices.size() + " dispositivos"));
    }

    private void getContent()
    {
        userItems = ViewManager.conn.getItems();

        for (Items item : userItems)
            if(item.getFav())
                userFavorites.add(item);

        executorService.execute(this::getAllContent);
    }

    private void getAllContent()
    {
        scrollItemContainer.setAlignment(Pos.TOP_LEFT);
        Platform.runLater(() -> scrollItemContainer.getChildren().clear());

        executorService.execute(this::getPasswords);
        executorService.execute(this::getNotes);
    }

    private void getPasswords()
    {
        for (Items i : userItems)
        {
            if(i.getPasswordsByIdI() != null)
                Platform.runLater(() -> scrollItemContainer.getChildren().add(
                        generateItemCard(i)
                ));
        }
    }

    private void getNotes()
    {
        for (Items i : userItems)
        {
            if(i.getNotesByIdI() != null)
                Platform.runLater(() -> scrollItemContainer.getChildren().add(
                        generateItemCard(i)
                ));
        }
    }

    private void getFavorites()
    {
        for (Items i : userFavorites)
            Platform.runLater(() -> scrollItemContainer.getChildren().add(generateItemCard(i)));

    }

    public void removeItemFromArray(Items item)
    {
        userItems.remove(item);

        if(userFavorites.contains(item))
            userFavorites.remove(item);
    }

    public void updateItemFromArray(Items items)
    {
        if(userFavorites.contains(selectedItem))
            userFavorites.set(userFavorites.indexOf(selectedItem), items);

        userItems.set(userItems.indexOf(selectedItem), items);
    }

    public void addItemToArray(Items items){
        userItems.add(items);
    }

    public static void changeNodeVisibility(boolean visible, Node... nodes)
    {
        for (Node node : nodes)
        {
            if(node != null)
            {
                node.setVisible(visible);
                node.setManaged(visible);
            }

        }
    }

    public void displayGenerator(CreateUpdateController controller, String fxml) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("views/" + fxml), ViewManager.bundle);
        HBox modal = loader.load();

        GeneratorController generatorController = loader.getController();
        generatorController.initialize(controller);

        displayModalWindow(modal);
    }

    @FXML
    public void displayConfig() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("views/config-view.fxml"), ViewManager.bundle);
        VBox modal = loader.load();

        ConfigController configController = loader.getController();
        configController.initialize(this, userDevices);

        displayModalWindow(modal);
    }

    public void removeModal()
    {
        if(mainBody.getChildren().size() != 1)
            mainBody.getChildren().remove(1);
    }

    public void displayModalWindow(Node modal)
    {
        if(mainBody.getChildren().size() == 1)
        {
            modal.toFront();
            mainBody.getChildren().add(modal);
        }
    }
}