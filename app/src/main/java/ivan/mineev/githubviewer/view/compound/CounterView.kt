package ivan.mineev.githubviewer.view.compound

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import ivan.mineev.githubviewer.R

class CounterView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : TextWithIconView(context, attributeSet, defStyleAttr, defStyleRes, R.layout.view_counter) {
    private val valueView: TextView = findViewById(R.id.value)
    private val defaultValueSizeSp = 14f

    private val defaultValueColor by lazy {
        ContextCompat.getColor(context, R.color.black)
    }

    init {
        attributeSet?.let { applyCounterAttr(context, it) }
    }

    private fun applyCounterAttr(context: Context, attributeSet: AttributeSet) {
        context.obtainStyledAttributes(attributeSet, R.styleable.CounterView).apply {
            try {
                renderValue(this)
            } finally {
                recycle()
            }
        }
    }

    private fun renderValue(typedArray: TypedArray) {

        val valueText = typedArray.getString(R.styleable.CounterView_valueText)
            ?: error("TextWithIconView: app:valueText is required when value is show")

        val valueColor =
            typedArray.getColor(R.styleable.CounterView_valueColor, defaultValueColor)

        val valueSize =
            typedArray.getDimension(R.styleable.CounterView_valueSize, defaultValueSizeSp)

        valueView.apply {
            text = valueText
            setTextColor(valueColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, valueSize)
        }
    }

}