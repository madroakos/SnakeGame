module com.madroakos.snakegame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.madroakos.snakegame to javafx.fxml;
    exports com.madroakos.snakegame;
}