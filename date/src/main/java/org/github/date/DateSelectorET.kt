package org.github.date

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

const val DEFAULT_DATE_FORMAT_INDEX = 0

class DateSelectorET : FrameLayout, View.OnTouchListener, DatePickerDialog.OnDateSetListener {

    /**
     * Date formats
     * appearance of text in edit text
     */
    private val dateFormats = arrayListOf(
        "yyyy-MM-dd",
        "yyyy-dd-MM",
        "MM-dd-yyyy",
        "MM-yyyy-dd",
        "dd-yyyy-MM",
        "dd-MM-yyyy"
    )

    /**
     * Date patterns
     * */
    private val PATTERN = arrayListOf(
        "^[0-9]{4}-(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])\$",
        "^[0-9]{4}-(3[01]|[12][0-9]|0[1-9])-(1[0-2]|0[1-9])\$",
        "^(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])-[0-9]{4}\$",
        "^(1[0-2]|0[1-9])-[0-9]{4}-(3[01]|[12][0-9]|0[1-9])\$",
        "^(3[01]|[12][0-9]|0[1-9])-[0-9]{4}-(1[0-2]|0[1-9])\$",
        "^(3[01]|[12][0-9]|0[1-9])-(1[0-2]|0[1-9])-[0-9]{4}\$"
    )

    private var mTextLayout: TextInputLayout? = null
    private var mEditText: TextInputEditText? = null

    private var mHintText: String? = ""
    private var mContentText: String? = ""
    private var mMinDate: String? = ""
    private var mMaxDate: String? = ""

    /**
     * Date Format
     * default format : yyyy-mm-dd
     * available formats: [yyyy-mm-dd, yyyy-dd-mm, mm-dd-yyyy, mm-yyyy-dd, dd-yyyy-mm, dd-mm-yyyy]
     * */
    private var mDateFormatValue: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        attrs?.let {
            val typedArray = this.context.obtainStyledAttributes(attrs, R.styleable.DateSelectorET)
            try {
                mDateFormatValue = typedArray.getInt(
                    R.styleable.DateSelectorET_dateFormat,
                    DEFAULT_DATE_FORMAT_INDEX
                )
                mHintText = typedArray.getString(R.styleable.DateSelectorET_android_hint)
                mContentText = typedArray.getString(R.styleable.DateSelectorET_android_text)
                mMinDate = typedArray.getString(R.styleable.DateSelectorET_minDate)
                mMaxDate = typedArray.getString(R.styleable.DateSelectorET_maxDate)
            } finally {
                typedArray.recycle()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        mEditText?.setOnTouchListener(this)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.action?.let {
            if (it == MotionEvent.ACTION_UP) {
                showDatePicker()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun initView() {
        this.addView(LayoutInflater.from(context).inflate(R.layout.date_selector, this, false))

        try {
            val v = getChildAt(0)
            if (v is TextInputLayout) mTextLayout = v
            else throw Exception("TextInputLayout didn't instantiated properly")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val v = mTextLayout?.editText
            if (v is TextInputEditText) mEditText = v
            else throw Exception("TextInputEditText didn't instantiated properly")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        initProps()
    }

    private fun initProps() {
        mEditText?.isFocusable = false
        this.hint = mHintText
        mContentText?.let {
            if (!TextUtils.isEmpty(it))
                this.text = it
        }
        mMinDate?.let {
            if (!TextUtils.isEmpty(it))
                this.minDate = it
        }
        mMaxDate?.let {
            if (!TextUtils.isEmpty(it))
                this.maxDate = it
        }
        if (!isInEditMode) initEvent()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    /**
     * Prepare and display DatePickerDialog
     * */
    private fun showDatePicker() {
        val calendar = getCalendarInstance()
        val datePicker = DatePickerDialog(
            context,
            this,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        datePicker.setCancelable(false)
        setMinMaxDate(datePicker).show()
    }

    private fun setMinMaxDate(datePicker: DatePickerDialog): DatePickerDialog {
        mMinDate?.let {
            if (!TextUtils.isEmpty(it))
                datePicker.datePicker.minDate = getTimeInMillis(it)
        }
        mMaxDate?.let {
            if (!TextUtils.isEmpty(it))
                datePicker.datePicker.maxDate = getTimeInMillis(it)
        }
        return datePicker
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = getCalendarInstance()
        calendar.set(year, month, dayOfMonth)
        this.text = formatDate(calendar)
    }

    private fun getCalendarInstance(): Calendar {
        val calendar = Calendar.getInstance()
        text?.let {
            if (!TextUtils.isEmpty(it))
                calendar.timeInMillis = getTimeInMillis(it)
        }
        return calendar
    }

    private fun isValidDate(date: String): Boolean {
        val matcher = DateMatcher()
        if (!matcher.matches(date, PATTERN[mDateFormatValue]))
            throw Exception("Invalid date format supplied")
        return true
    }

    interface Matcher {
        fun matches(date: String, PATTERN: String): Boolean
    }

    /**
     * Convert date to readable text
     * */
    private fun formatDate(calendar: Calendar): String {
        var date = ""
        try {
            val pattern = getCurrentDatePattern()
            date = SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * Get timeInMillis from readable text
     * */
    private fun getTimeInMillis(text: String): Long {
        try {
            if (TextUtils.isEmpty(text))
                return System.currentTimeMillis()
            val pattern = getCurrentDatePattern()
            val date: Date? = SimpleDateFormat(pattern, Locale.getDefault()).parse(text)
            return date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            return System.currentTimeMillis()
        }
    }

    /**
     * Get current date pattern
     * */
    private fun getCurrentDatePattern(): String {
        return dateFormats[mDateFormatValue]
    }

    /**
     * Getter and setter for
     * hint
     * */
    var hint: String?
        set(value) {
            mEditText?.hint = value ?: ""
        }
        get() = mEditText?.hint?.toString() ?: ""

    /**
     * Getter and setter for
     * text
     * */
    var text: String?
        set(value) {
            val contentText = value ?: ""
            if (!isValidDate(contentText))
                return
            val editable = Editable.Factory()
            mEditText?.text = editable.newEditable(contentText)
        }
        get() = mEditText?.text?.toString() ?: ""

    /**
     * Setter for
     * minDate
     * which sets a minimum date for calendar in specified ]date range
     * */
    var minDate: String = ""
        set(value) {
            if (!isValidDate(value))
                return
            this.mMinDate = value
        }

    /**
     * Setter for
     * maxDate
     * which sets a maximum date for calendar in specified ]date range
     * */
    var maxDate: String = ""
        set(value) {
            if (!isValidDate(value))
                return
            this.mMaxDate = value
        }

}