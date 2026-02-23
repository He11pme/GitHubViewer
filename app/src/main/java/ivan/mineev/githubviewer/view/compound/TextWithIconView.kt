package ivan.mineev.githubviewer.view.compound

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import ivan.mineev.githubviewer.R

/**
 * Compound View для текста с иконкой.
 *
 * Реализация появилась до того, как я узнал о встроенной поддержке иконок в TextView.
 *
 * Осознанно не стал переделывать компонент, так как:
 * - он уже используется в нескольких местах
 * - соответствует принципу переиспользуемости UI-компонентов
 *
 */
open class TextWithIconView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    @LayoutRes layout: Int = R.layout.view_text_with_icon
) : LinearLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    // VIEWS
    private val iconView: ImageView

    private val labelView: TextView

    // DEFAULT VALUE ATTRIBUTES

    private val defaultIconSize = 20.dp

    private val defaultLabelSizeSp = 14f
    private val defaultIconColor by lazy {
        ContextCompat.getColor(context, R.color.text)
    }
    private val defaultLabelColor by lazy {
        ContextCompat.getColor(context, R.color.text)
    }

    // DYNAMIC ATTRIBUTES

    var labelText: String?
        get() = labelView.text.toString()
        set(value) {
            labelView.text = value
        }

    init {
        LayoutInflater.from(context).inflate(layout, this, true)
        this.orientation = HORIZONTAL

        iconView = findViewById<ImageView>(R.id.icon)
        labelView = findViewById<TextView>(R.id.label)

        attributeSet?.let { applyBaseAttr(context, it) }

    }

    private fun applyBaseAttr(context: Context, attributeSet: AttributeSet) {
        context.obtainStyledAttributes(attributeSet, R.styleable.TextWithIconView).apply {
            try {
                renderIcon(this)
                renderLabel(this)
            } finally {
                recycle()
            }
        }
    }

    private fun renderIcon(typedArray: TypedArray) {

        val icon = typedArray.getDrawable(R.styleable.TextWithIconView_icon)
            ?: error("TextWithIcon: app:icon is required")

        val iconColor =
            typedArray.getColor(R.styleable.TextWithIconView_iconColor, defaultIconColor)

        val iconSize =
            typedArray.getDimension(R.styleable.TextWithIconView_iconSize, defaultIconSize)

        iconView.apply {
            setImageDrawable(icon)
            setColorFilter(iconColor)
            layoutParams = layoutParams.apply {
                width = iconSize.toInt()
                height = iconSize.toInt()
            }
        }
    }

    private fun renderLabel(typedArray: TypedArray) {

        labelText = typedArray.getString(R.styleable.TextWithIconView_labelText) ?: ""

        val labelColor =
            typedArray.getColor(R.styleable.TextWithIconView_labelColor, defaultLabelColor)

        val labelSize =
            typedArray.getDimension(R.styleable.TextWithIconView_labelSize, defaultLabelSizeSp)

        labelView.apply {
            text = labelText
            setTextColor(labelColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, labelSize)
        }
    }

    private val Int.dp: Float
        get() = this * resources.displayMetrics.density

}

