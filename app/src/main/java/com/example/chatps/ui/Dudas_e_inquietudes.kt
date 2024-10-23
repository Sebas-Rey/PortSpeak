package com.example.chatps.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.example.chatps.R

class dudas_e_inquietudes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dudas_einquietudes)

        val layoutMessages = findViewById<LinearLayout>(R.id.layoutMessages)
        val etUserInput = findViewById<EditText>(R.id.etUserInput)
        val btnSend = findViewById<Button>(R.id.btnSend)

        // Agregar el mensaje de bienvenida al iniciar la actividad
        val welcomeMessage = "Bienvenido al chatbot de Portspeak. Soy un asistente virtual que te va a ayudar con tus dudas e inquietudes respecto a la app. Dime en qué puedo ayudarte."
        addMessage(layoutMessages, "Chatbot: $welcomeMessage", false)

        btnSend.setOnClickListener {
            val userInput = etUserInput.text.toString().trim()

            if (userInput.isNotEmpty()) {
                addMessage(layoutMessages, "Tú: $userInput", true)
                val response = getResponse(userInput)
                addMessage(layoutMessages, "Chatbot: $response", false)
                etUserInput.text.clear()
            }
        }

        findViewById<AppCompatImageView>(R.id.imageHome).setOnClickListener() {
            finish()
        }
    }

    private fun addMessage(layout: LinearLayout, message: String, isUser: Boolean) {
        val cardView = CardView(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(0, 10, 0, 10)
        if (isUser) {
            layoutParams.gravity = android.view.Gravity.END
            cardView.setCardBackgroundColor(getColor(R.color.primary))
        } else {
            layoutParams.gravity = android.view.Gravity.START
            cardView.setCardBackgroundColor(getColor(R.color.white))
        }
        cardView.layoutParams = layoutParams
        cardView.radius = 16f

        val textView = TextView(this)
        textView.text = message
        textView.setPadding(16, 16, 16, 16)

        if (isUser) {
            textView.setTextColor(getColor(R.color.white)) // Color del texto del usuario
        } else {
            textView.setTextColor(getColor(R.color.black)) // Color del texto del chatbot
        }

        cardView.addView(textView)
        layout.addView(cardView)
    }

    private fun getResponse(userInput: String): String {
        return when {
            userInput.contains("hola", ignoreCase = true) ||
                    userInput.contains("b", ignoreCase = true) ||
                    userInput.contains("buenas", ignoreCase = true) ||
                    userInput.contains("buenos dias", ignoreCase = true) ||
                    userInput.contains("buenas tardes", ignoreCase = true) ||
                    userInput.contains("buenas noches", ignoreCase = true) ->
                "¡Hola! ¿Cómo puedo ayudarte? Para ver las preguntas frecuentes, escribe la palabra 'preguntas'."

            userInput.equals("preguntas", ignoreCase = true) ->
                "\n1. Inicio ¿Cómo ir a los apartados de la aplicación?\n" +
                        "2. Perfil ¿Cómo actualizar la información en la app?\n" +
                        "3. Parqueadero ¿Cómo puedo saber el lugar de parqueo que me corresponde?\n" +
                        "4. Pago de administración ¿Cómo realizo el pago de administración?\n" +
                        "5. Mascotas ¿Cómo accionar el botón de alerta para mascotas?\n" +
                        "6. Visitantes ¿Cómo puedo visualizar mi historial de visitas?\n" +
                        "7. Chat ¿Cómo funciona el chat?\n" +
                        "8. Llamadas ¿Cómo realizo una llamada desde la app?\n" +
                        "Por favor elige una opción del 1 al 8."

            userInput.equals("1") -> "Respuesta: Te puedes dirigir a los apartados de la aplicación dando click al ícono del círculo con puntos suspensivos ascendentes ubicado en la pantalla de inicio en la parte derecha inferior."
            userInput.equals("2") -> "Respuesta: Dirígete al menú desplegable, elige el apartado de la aplicación donde deseas cambiar tus datos, y haz clic en el botón actualizar datos. Luego ingresa nuevamente la información correspondiente."
            userInput.equals("3") -> "Respuesta: Dirígete al menú desplegable, elige el apartado de Parqueadero, y allí encontrarás un campo con tu lugar de parqueo correspondiente."
            userInput.equals("4") -> "Respuesta: Dirígete al menú desplegable, elige el apartado de Pago de Administración, y allí encontrarás una pasarela de pago con Bancolombia, PSE y Nequi. Elige uno y realiza el pago correspondiente."
            userInput.equals("5") -> "Respuesta: Dirígete al menú desplegable, elige el apartado de Mascotas, selecciona la mascota perdida y oprime el botón 'Perdí mi Mascota'."
            userInput.equals("6") -> "Respuesta: Dirígete al menú desplegable, elige el apartado de control de visitantes y allí podrás visualizar todo el historial de tus visitas."
            userInput.equals("7") -> "Respuesta: Dirígete al apartado de chat de mensajería, elige un usuario o agrégalo usando el botón de personas en la esquina derecha inferior. En caso de que no lo tengas agregado, aparecerá una lista de los usuarios con los que puedes interactuar. Elige el chat y podrán interactuar en sincronía."
            userInput.equals("8") -> "Respuesta: Dirígete al apartado de llamadas, elige el número al que deseas llamar y haz clic en el ícono del teléfono."

            userInput.contains("gracias", ignoreCase = true) ||
                    userInput.contains("ok", ignoreCase = true) -> "¡Adiós! Que tengas un buen día."

            else -> "Lo siento, no entiendo tu pregunta."
        }
    }
}
