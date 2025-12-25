module com.sosyalag.bngsocial3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.sosyalag.bngsocial3 to javafx.fxml;
    exports com.sosyalag.bngsocial3;
    exports com.sosyalag.bngsocial3.Model;
    opens com.sosyalag.bngsocial3.Model to javafx.fxml;
}