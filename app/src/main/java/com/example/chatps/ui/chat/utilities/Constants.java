package com.example.chatps.ui.chat.utilities;

import java.util.HashMap;

public class Constants {

    //Users
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_CEDULA = "cedula";
    public static final String KEY_VIVIENDA = "vivienda";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ROL = "userRol";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";

    //Chat
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";

    //Mensajes
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    //Mascotas
    public static final String KEY_COLLECTION_PET = "pets";
    public static final String KEY_PET_ID = "petId";
    public static final String KEY_PET_IMAGE = "petImage";
    public static final String KEY_PET_NAME = "petName";
    public static final String KEY_PET_CLASS = "petClass";
    public static final String KEY_PET_RAZA = "petRaza";
    public static final String KEY_PET_SEXO = "petSexo";
    public static final String KEY_PET_VACUNAS = "petVacunas";
    public static final String KEY_PET = "pet";
    public static final String KEY_PET_OWNER_ID = "petOwnerId";

    //Parqueadero
    public static final String KEY_COLLECTION_VEHICLE = "vehicles";
    public static final String KEY_VEHICLE_ID = "vehicleId";
    public static final String KEY_VEHICLE = "vehicle";
    public static final String KEY_VEHICLE_IMAGE = "vehicleImage";
    public static final String KEY_VEHICLE_TYPE = "vehicleType";
    public static final String KEY_VEHICLE_PLACA = "vehiclePlaca";
    public static final String KEY_VEHICLE_MODEL = "vehicleModel";
    public static final String KEY_VEHICLE_COLOR = "vehicleColor";
    public static final String KEY_VEHICLE_MARCA = "vehicleMarca";
    public static final String KEY_VEHICLE_PARKING = "vehicleParking";
    public static final String KEY_VEHICLE_OWNER_ID = "vehicleOwnerId";

    //Visitantes
    public static final String KEY_COLLECTION_VISITANTES = "visitantes";
    public static final String KEY_VISITANTES_ID = "visitId";
    public static final String KEY_VISITANTES = "visit";
    public static final String KEY_VISITANTES_NAME = "visitName";
    public static final String KEY_VISITANTES_APARTMENT = "visitApartment";
    public static final String KEY_VISITANTES_TIME_ENTER = "visitTimeEnter";
    public static final String KEY_VISITANTES_TIME_EXIT = "visitTimeExit";
    public static final String KEY_VISITANTES_PHONE = "visitPhone";
    public static final String KEY_VISITANTES_TYPE_AUTOMOBILE = "visitVehicle";
    public static final String KEY_VISITANTES_CARACTER_AUTOMOBILE = "visitCaracterVehicle";
    public static final String KEY_VISITANTES_USER_ID = "visitUserId";

    //Administración
    public static final String KEY_COLLECTION_ADMIN = "administracion";
    public static final String KEY_ADMIN_ID = "adminId";
    public static final String KEY_ADMIN_USER = "adminUser";
    public static final String KEY_ADMIN_APARTMENT = "adminApto";
    public static final String KEY_ADMIN_DATE = "adminDate";
    public static final String KEY_ADMIN_PAY = "adminPay";
    public static final String KEY_ADMIN_PAY_METHOD = "adminPayMethod";
    public static final String KEY_ADMIN_USER_ID = "adminUserId";


    public static HashMap<String, String> remoteMsgHeaders = null;
    public static final HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAVOuF810:APA91bEaXmxinmmV-9A926BfTLZHyUr6g21nwqhmWRrxBLXHytoE9lvm5nGSqSNTsePrtxLybJ_Ay6_bA9JH22FMj-CnBs77vv0N2Vjuv_Tsldsd1v44q3QYnq8FaOzJe1N5edQtFp7S"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
