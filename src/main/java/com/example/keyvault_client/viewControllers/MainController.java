package com.example.keyvault_client.viewControllers;
import com.example.keyvault_client.Controllers.Config;
import com.example.keyvault_client.Controllers.ConnectionController;
import com.example.keyvault_client.NodeGenerator;
import com.example.keyvault_client.ViewManager;
import com.keyvault.database.models.Devices;
import com.keyvault.database.models.Items;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainController {

    @FXML
    public VBox scrollItemContainer;
    @FXML
    public Label userNameLabel, allButton, menuFavorites, menuPasswords, menuNotes, menuSettings, closeSession, categoriesTitle;
    @FXML
    public TextField searchField;
    @FXML
    public StackPane mainBody;
    @FXML
    public HBox topMenuContainer,infoContainer, messageContainer;
    @FXML
    public ImageView profileImage;
    public Label selectedMenu, messageLabel, devicesLabel;
    private List<Items> userItems = new ArrayList<>();
    private List<Items> userFavorites = new ArrayList<>();
    private List<Devices> userDevices = new ArrayList<>();
    private final Image fillStar = new Image(ViewManager.class.getResourceAsStream("icons/starFill.png"));
    private final Image emptyStar = new Image(ViewManager.class.getResourceAsStream("icons/star.png"));
    private Items selectedItem = null;
    private CreateUpdateController createUpdateController = null;
    private ExecutorService executorService;
    private ConnectionController connectionController;
    private ResourceBundle resourceBundle;

    public void initialize()
    {
        this.executorService = ViewManager.executorService;
        this.connectionController = ViewManager.conn;
        this.resourceBundle = ViewManager.bundle;

        this.executorService.execute(this::getContent);
        this.executorService.execute(this::getDevices);

        userNameLabel.setText(connectionController.getEmail());
        scrollItemContainer.setAlignment(Pos.CENTER);
        scrollItemContainer.getChildren().add(new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS));

        selectedMenu = allButton;

        File image = new File(Config.configDir + "/" + connectionController.getEmail() + "-profileImage.png");

        try (InputStream inputStream = new FileInputStream(image))
        {
            profileImage.setImage(new Image(inputStream));
        }
        catch (NullPointerException | IOException ignored)
        {

        }

        setRoundedImage();
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

    private void setRoundedImage()
    {
        Rectangle clip = new Rectangle(60, 60);
        clip.setArcHeight(10d);
        clip.setArcWidth(10d);
        profileImage.setClip(clip);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = profileImage.snapshot(parameters, null);

        profileImage.setClip(null);
        profileImage.setImage(image);
    }

    @FXML
    public void closeSession()
    {
        connectionController.closeSession(true);
        executorService.shutdownNow();
    }

    @FXML
    public void searchItem()
    {
        String toSearch = searchField.getText().trim().toLowerCase();

        if(!toSearch.isBlank() && !toSearch.isEmpty())
        {
            scrollItemContainer.getChildren().clear();

            for (Items i : userItems)
                if (i.getName().toLowerCase().contains(toSearch))
                   scrollItemContainer.getChildren().add(generateItemCard(i));
        }
        else
        {
            changeMenu(selectedMenu);
        }
    }
    public void changeLanguage(ResourceBundle bundle)
    {
        resourceBundle = bundle;

        allButton.setText(resourceBundle.getString("allItems"));
        menuFavorites.setText(resourceBundle.getString("favorites"));
        searchField.setPromptText(resourceBundle.getString("search"));
        categoriesTitle.setText(resourceBundle.getString("categories"));
        menuPasswords.setText(resourceBundle.getString("passwords"));
        menuNotes.setText(resourceBundle.getString("notes"));
        menuSettings.setText(resourceBundle.getString("configuration"));
        closeSession.setText(resourceBundle.getString("closeSession"));
        devicesLabel.setText(resourceBundle.getString("devices") + ": " + userDevices.size());
    }

    @FXML
    public void changeUserImage()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Imagen de perfil");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (*.png , *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(ViewManager.getStage());

        executorService.execute(() -> {
            int response = connectionController.sendImage(file);

            if(response == 200)
            {
                File image = new File(Config.configDir + "/" + connectionController.getEmail() + "-profileImage.png");

                Platform.runLater(() -> {
                    try (InputStream inputStream = new FileInputStream(image)) {
                        profileImage.setImage(new Image(inputStream));
                        setRoundedImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        });
    }

    @FXML
    public void showCreateView()
    {
        addViewToInfoContainer(false);
    }

    public void showEditView()
    {
        addViewToInfoContainer(true);
    }

    public void showMessage(String text, boolean isError)
    {
        if(isError)
            messageContainer.getStyleClass().add("messageError");
        else
            messageContainer.getStyleClass().remove("messageError");

        ViewManager.displayMessage(text, messageContainer, messageLabel, null);
    }

    private void showItemInfo(Items target)
    {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("views/item-view.fxml"), resourceBundle);
            HBox box = loader.load();

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

    private void addViewToInfoContainer(boolean isEdit)
    {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("views/create-update-view.fxml"), resourceBundle);
            HBox box = loader.load();

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
        Button editButton = NodeGenerator.generateActionButton("icons/pencil.png", null, "actionButton");
        Button deleteButton = NodeGenerator.generateActionButton("icons/trash.png", null, "actionButton");
        Button favButton = NodeGenerator.generateActionButton(selectedItem.getFav() ? "icons/starFill.png" : "icons/star.png", null, "actionButton");
        Button reloadButton = NodeGenerator.generateActionButton("icons/reloadDark.png", null, "actionButton");

        deleteButton.setOnMouseClicked((e) -> executorService.execute(this::deleteItem));
        favButton.setOnMouseClicked((e) -> changeFav(favButton));
        editButton.setOnMouseClicked((e) -> showEditView());
        reloadButton.setOnMouseClicked((e) -> reload());
        addButtonsTopMenu(Pos.CENTER_RIGHT, editButton, deleteButton, favButton, reloadButton);
    }

    private void addReloadButton()
    {
        Button reloadButton = NodeGenerator.generateActionButton("icons/reloadDark.png", null, "actionButton");
        reloadButton.setOnMouseClicked((e) -> reload());
        addButtonsTopMenu(Pos.CENTER_RIGHT, reloadButton);
    }

    private void reload()
    {
        scrollItemContainer.getChildren().clear();
        scrollItemContainer.setAlignment(Pos.CENTER);
        scrollItemContainer.getChildren().add(new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS));
        this.executorService.execute(this::getContent);
        this.executorService.execute(this::getDevices);
        infoContainer.getChildren().clear();
    }

    private void generateCreateActionButtons(boolean isEdit)
    {
        Button cancel = NodeGenerator.generateActionButton("icons/x.png", "   Cancelar", "actionButton");
        Button accept = NodeGenerator.generateActionButton("icons/tick.png", "   Aceptar", "actionButton");

        accept.getStyleClass().add("actionButton");
        cancel.getStyleClass().add("actionButton");

        accept.setOnMouseClicked((e) -> executorService.execute(isEdit ? this::updateItem : this::insertItem));
        cancel.setOnMouseClicked((e) -> {
            Platform.runLater(() -> infoContainer.getChildren().clear());
            topMenuContainer.getChildren().clear();
            addReloadButton();
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
        executorService.execute(() -> consumeOperation("create", newItem, connectionController::insertItem, this::addItemToArray));
    }

    private void deleteItem()
    {
        executorService.execute(() -> consumeOperation("delete", selectedItem, connectionController::deleteItem, this::removeItemFromArray));
    }

    private void updateItem()
    {
        Items modItem = createUpdateController.updateItem();
        modItem.setIdI(selectedItem.getIdI());

        executorService.execute(() -> consumeOperation("update", modItem, connectionController::modItem, this::updateItemFromArray));
    }

    private void consumeOperation(String successMessage, Items item, Function<Items, Integer> operation, Consumer<Items> arrayOpertation)
    {
        int response = operation.apply(item);
        Platform.runLater(() -> {
            switch (response) {
                case 200 -> {
                    arrayOpertation.accept(item);
                    this.showMessage(successMessage , false);
                    this.reloadView();

                    if(successMessage.equals("update") || successMessage.equals("create"))
                    {
                        selectedItem = item;
                        showItemInfo(item);
                    }

                    if(successMessage.equals("create"))
                        selectedItem.setIdI((Integer) connectionController.getApi().getResponseContent());

                }
                case 201 -> connectionController.closeSession(false);
                default -> this.showMessage("message" + response, true);
            }
        });

    }

    public void reloadView()
    {
        selectedItem = null;
        infoContainer.getChildren().clear();
        topMenuContainer.getChildren().clear();
        scrollItemContainer.getChildren().clear();
        addReloadButton();

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
            userFavorites.add(selectedItem);
        else
            userFavorites.remove(selectedItem);

        executorService.execute(() -> connectionController.changeFav(selectedItem, this));
    }

    private void getDevices()
    {
        userDevices = connectionController.getDevices();
        Platform.runLater(() -> devicesLabel.setText(resourceBundle.getString("devices") + ": " + userDevices.size()));
    }

    private void getContent()
    {
        userItems = connectionController.getItems();

        for (Items item : userItems)
            if(item.getFav())
                userFavorites.add(item);

        executorService.execute(this::getAllContent);
    }

    private void getAllContent()
    {
        scrollItemContainer.setAlignment(Pos.TOP_LEFT);

        Platform.runLater(() ->
        {
            scrollItemContainer.getChildren().clear();

            for (Items i : userItems)
                scrollItemContainer.getChildren().add(generateItemCard(i));
        });
    }

    private void getPasswords()
    {
        Platform.runLater(() ->
        {
            for (Items i : userItems)
                if(i.getPasswordsByIdI() != null)
                    scrollItemContainer.getChildren().add(generateItemCard(i));
        });

    }

    private void getNotes()
    {
        Platform.runLater(() ->
        {
            for (Items i : userItems)
                if(i.getNotesByIdI() != null)
                    scrollItemContainer.getChildren().add(generateItemCard(i));
        });
    }

    private void getFavorites()
    {
        Platform.runLater(() ->
        {
            for (Items i : userFavorites)
                scrollItemContainer.getChildren().add(generateItemCard(i));
        });
    }

    public void removeItemFromArray(Items item)
    {
        userItems.remove(item);
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

    public void displayGenerator(CreateUpdateController controller) throws IOException
    {
        displayModalWindow("views/generator-view.fxml", loader -> {
            GeneratorController generatorController = loader.getController();
            generatorController.initialize(controller);
        });
    }

    @FXML
    public void displayConfig() throws IOException
    {
        displayModalWindow("views/config-view.fxml", loader -> {
            ConfigController configController = loader.getController();
            configController.initialize(this, userDevices);
        });
    }

    public void displayVerifyTotp() throws IOException
    {
        displayModalWindow("views/verify-totp-view.fxml", loader -> {
            VerifyTotpController controller = loader.getController();
            controller.initialize(this);
        });
    }

    private void displayModalWindow(String path, Consumer<FXMLLoader> controller) throws IOException {
        if(mainBody.getChildren().size() == 1)
        {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(path), resourceBundle);
            VBox modal = loader.load();

            controller.accept(loader);

            modal.toFront();
            mainBody.getChildren().add(modal);
        }
    }

    public void removeModal()
    {
        if(mainBody.getChildren().size() != 1)
            mainBody.getChildren().remove(1);
    }
}