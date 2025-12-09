package dev.slne.surf.shulker.api.player

import com.google.gson.*
import dev.slne.surf.shulker.api.ShulkerKeys
import java.lang.reflect.Type
import java.util.*

object PlayerSerializer : JsonSerializer<ShulkerPlayer>, JsonDeserializer<ShulkerPlayer> {
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): ShulkerPlayer {
        val jsonObj = json.asJsonObject

        val uuid = UUID.fromString(jsonObj.get(ShulkerKeys.UNIQUE_ID).asString)
        val name = jsonObj.get(ShulkerKeys.NAME).asString
        val currentServerName = jsonObj.get(ShulkerKeys.CURRENT_SERVER_NAME).asString

        return ShulkerPlayer(
            uuid = uuid,
            name = name,
            currentServiceName = currentServerName
        )
    }

    override fun serialize(
        player: ShulkerPlayer,
        type: Type,
        context: JsonSerializationContext
    ) = JsonObject().apply {
        addProperty(ShulkerKeys.UNIQUE_ID, player.uniqueId)
        addProperty(ShulkerKeys.NAME, player.name)
        addProperty(ShulkerKeys.CURRENT_SERVER_NAME, player.currentServiceName)
    }
}