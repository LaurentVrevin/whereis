package com.laurentvrevin.androidstarter.data.network

/**
 * Interface scellée pour les erreurs réseau.
 * Permet de transporter plus de contexte qu'une simple enum.
 */
sealed interface NetworkError {
    data object RequestTimeout : NetworkError

    data object Unauthorized : NetworkError

    data object Forbidden : NetworkError

    data object NotFound : NetworkError

    data object PayloadTooLarge : NetworkError

    data object ServerError : NetworkError

    data object Serialization : NetworkError

    data object NoInternet : NetworkError

    data class Unknown(val message: String? = null) : NetworkError

    // Pour la compatibilité avec l'affichage simple
    val name: String
        get() =
            when (this) {
                is Unknown -> "UNKNOWN"
                else -> this::class.simpleName?.uppercase() ?: "UNKNOWN"
            }
}
