package id.jostudios.penielcommunity.Helpers

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptorHelper {
    private val KEY = "SSO-KEY2024!raw_";
    private val ALGORITHM = "AES/CBC/PKCS5Padding"
    private val ivSpec = IvParameterSpec(ByteArray(16));
    private val keySpec = SecretKeySpec(KEY.toByteArray(), ALGORITHM);

    public fun Encrypt(data: String): String {
        val cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        val encrypted = cipher.doFinal(data.toByteArray());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public fun Decrypt(data: String): String? {
        try {
            val cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            val cipherData = Base64.getDecoder().decode(data);
            val decrypted = cipher.doFinal(cipherData);
            return String(decrypted);
        } catch (e: Exception) {
            return null;
        }
    }

    public fun Hash(data: String): String? {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}