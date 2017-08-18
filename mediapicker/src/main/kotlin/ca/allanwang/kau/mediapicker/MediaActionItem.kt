package ca.allanwang.kau.mediapicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.permissions.PERMISSION_WRITE_EXTERNAL_STORAGE
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.materialDialog
import ca.allanwang.kau.utils.string
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import java.io.File


/**
 * Created by Allan Wang on 2017-08-17.
 */
class MediaActionItem(
        val type: MediaActionFrame,
        val color: Int = MediaPickerCore.accentColor
) : KauIItem<MediaActionItem, MediaItemBasic.ViewHolder>(R.layout.kau_iitem_image_basic, { MediaItemBasic.ViewHolder(it) }, R.id.kau_item_media_action) {

    lateinit var mediaType: MediaType

    override fun isSelectable(): Boolean = false

    override fun bindView(holder: MediaItemBasic.ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.image.apply {
            setImageDrawable(MediaPickerCore.getIconDrawable(context, type.iicon, color))
            setOnClickListener { type.invoke(context, this@MediaActionItem) }
        }
    }

    override fun unbindView(holder: MediaItemBasic.ViewHolder) {
        super.unbindView(holder)
        holder.image.apply {
            setImageDrawable(null)
            setOnClickListener(null)
        }
    }
}

interface MediaActionFrame {
    val iicon: IIcon
    fun invoke(c: Context, item: MediaActionItem)
}

internal const val MEDIA_ACTION_REQUEST = 100

class MediaActionCamera(val fileCreator: (Context) -> File) : MediaActionFrame {

    override val iicon = GoogleMaterial.Icon.gmd_photo_camera

    override fun invoke(c: Context, item: MediaActionItem) {
        c.kauRequestPermissions(PERMISSION_WRITE_EXTERNAL_STORAGE) {
            granted, _ ->
            if (granted) {
                val camera = Intent(item.mediaType.captureType)
                if (camera.resolveActivity(c.packageManager) == null) {
                    c.materialDialog {
                        title(R.string.kau_no_camera_found)
                        content(R.string.kau_no_camera_found_content)
                    }
                    return@kauRequestPermissions
                }
                val file: File = try {
                    fileCreator(c)
                } catch (e: java.io.IOException) {
                    c.materialDialog {
                        title(R.string.kau_error)
                        content(R.string.kau_temp_file_creation_failed)
                    }
                    return@kauRequestPermissions
                }
                camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
                (c as Activity).startActivityForResult(camera, MEDIA_ACTION_REQUEST)
            }
        }
    }
}

class MediaActionImageGallery(val multiple: Boolean = false) : MediaActionFrame {
    override val iicon = GoogleMaterial.Icon.gmd_photo
    override fun invoke(c: Context, item: MediaActionItem) {
        val intent = Intent().apply {
            type = item.mediaType.mimeType
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple)
        }
        (c as Activity).startActivityForResult(
                Intent.createChooser(intent, c.string(R.string.kau_select_media)),
                MEDIA_ACTION_REQUEST)
    }
}