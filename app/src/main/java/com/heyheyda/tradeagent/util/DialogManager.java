package com.heyheyda.tradeagent.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.heyheyda.tradeagent.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DialogManager {

    public interface ClickListener {
        void onClick();
    }

    public enum DialogType {
        NOTIFY,
        CONFIRM,
        HINT,
        INPUT,
        SINGLE_CHOICE,
        MULTI_CHOICE,
        DATE_PICK,
        TIME_PICK
    }

    public enum DialogArgument {
        BOOLEAN_SKIP_HINT_CHECKED_STATE,
        BOOLEAN_TIME_PICK_VIEW_24_HOUR,
        INT_CHECKED_INDEX_SINGLE_CHOICE,
        INT_DAY_PICK_YEAR,
        INT_DAY_PICK_MONTH,
        INT_DAY_PICK_DAY,
        INT_TIME_PICK_HOUR,
        INT_TIME_PICK_MINUTE,
        STRING_DIALOG_TITLE,
        STRING_DIALOG_MESSAGE,
        STRING_DIALOG_MESSAGE_INPUT,
        STRING_DIALOG_MESSAGE_INPUT_HINT,
        STRING_DIALOG_MESSAGE_SKIP_HINT,
        STRING_BUTTON_TEXT_POSITIVE,
        STRING_BUTTON_TEXT_NEUTRAL,
        STRING_BUTTON_TEXT_NEGATIVE,
        DRAWABLE_DIALOG_ICON,
        BOOLEAN_ARRAY_CHECKED_STATE_MULTI_CHOICE,
        STRING_ARRAY_CHOICE_NAME
    }

    public enum DialogResult {
        BOOLEAN_CHECKED_STATE_SKIP_HINT,
        INT_CHECKED_INDEX_SINGLE_CHOICE,
        INT_DAY_PICK_YEAR,
        INT_DAY_PICK_MONTH,
        INT_DAY_PICK_DAY,
        INT_TIME_PICK_HOUR,
        INT_TIME_PICK_MINUTE,
        BOOLEAN_ARRAY_CHECKED_STATE_MULTI_CHOICE,
        STRING_DIALOG_MESSAGE_INPUT
    }

    public enum DialogStyle {
        DEFAULT,
        DARK_GRAY_BOTTOM
    }

    public enum ListenerType {
        POSITIVE,
        NEUTRAL,
        NEGATIVE,
        DATE_SET,
        TIME_SET
    }

    private static final String LOG_TAG = "DialogManager";
    private static final String ERROR_MESSAGE_INNER = "Class internal error.";
    private static final String ERROR_MESSAGE_INVALID_RESULT_TYPE = "Result name and type do not match.";
    private static final DialogType DEFAULT_DIALOG_TYPE = DialogType.NOTIFY;
    private static final int DEFAULT_SINGLE_CHOICE_CHECKED_INDEX = 0;
    private static final int DEFAULT_INPUT_LENGTH_LIMIT = 30;
    private static final boolean DEFAULT_HINT_SKIP_CHECKED_STATE = true;

    private HashMap<DialogStyle, Integer> dialogStyleResourceMap;
    private HashMap<DialogStyle, Integer> pickerDialogStyleResourceMap;
    private HashMap<DialogArgument, String> argumentFieldNameMap;
    private HashMap<DialogResult, String> resultFieldNameMap;

    private DialogType type;
    private DialogStyle style;
    private String title;
    private Drawable icon;
    private String message;
    private String[] list;
    private String positiveButtonText;
    private String neutralButtonText;
    private String negativeButtonText;
    private String skipHintMessage;
    private ClickListener positiveListener;
    private ClickListener neutralListener;
    private ClickListener negativeListener;
    private int inputLengthLimit;
    private int singleChoiceCheckedIndex;
    private boolean isHintSkipChecked;
    private String inputMessageHint;
    private String inputMessage;
    private boolean[] multiChoiceCheckedIndexList;
    private int datePickYear;
    private int datePickMonth;
    private int datePickDay;
    private int timePickHour;
    private int timePickMinute;
    private boolean is24HourView;
    private HashMap<DialogArgument, Boolean> booleanArguments;
    private HashMap<DialogArgument, Integer> intArguments;
    private HashMap<DialogArgument, String> stringArguments;
    private HashMap<DialogArgument, Drawable> drawableArguments;
    private HashMap<DialogArgument, boolean[]> booleanArrayArguments;
    private HashMap<DialogArgument, String[]> stringArrayArguments;

    private DialogManager() {
        initialDialogStyleResourceMap();
        initialArgumentFieldNameMap();
        initialResultFieldNameMap();
        initialDialogByDefaultValues();

        this.type = DEFAULT_DIALOG_TYPE;
        this.style = DialogStyle.DEFAULT;
        this.positiveListener = null;
        this.neutralListener = null;
        this.negativeListener = null;
    }

    public DialogManager(DialogType type, String title, String message) {
        this();

        this.type = type;
        setArgument(DialogArgument.STRING_DIALOG_TITLE, title);
        setArgument(DialogArgument.STRING_DIALOG_MESSAGE, message);
    }

    public void showDialog(@NonNull Context context) {
        final AlertDialog.Builder builder;

        boolean hasPositiveButton = false;
        boolean hasNeutralButton = false;
        boolean hasNegativeButton = false;
        boolean isKeyBoardAutoDisplay = false;

        //apply arguments before showing dialog
        applyArguments();

        //initial builder & set theme
        if (style == DialogStyle.DEFAULT) builder = new AlertDialog.Builder(context);
        else {
            int styleResource = dialogStyleResourceMap.get(style);
            builder = new AlertDialog.Builder(context, styleResource);
        }

        //set dialog title
        if (title != null) builder.setTitle(title);

        //set dialog icon
        if (icon != null) builder.setIcon(icon);

        //set dialog body
        View view;
        switch (type) {
            case NOTIFY:
                hasPositiveButton = true;
                if (message != null) builder.setMessage(message);
                break;
            case CONFIRM:
                hasPositiveButton = true;
                hasNegativeButton = true;
                if (message != null) builder.setMessage(message);
                break;
            case HINT:
                hasPositiveButton = true;

                view = View.inflate(context, R.layout.dialog_hint, null);

                if (message != null) {
                    TextView messageTextView = view.findViewById(R.id.message);
                    messageTextView.setText(message);
                }

                CheckBox skipCheckBox = view.findViewById(R.id.skipCheckBox);
                skipCheckBox.setChecked(isHintSkipChecked);
                skipCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        isHintSkipChecked = b;
                    }
                });
                if (skipHintMessage != null) skipCheckBox.setText(skipHintMessage);
                else skipCheckBox.setText(R.string.dialog_skip_hint_default_text);

                //set default button text
                if (positiveButtonText == null) {
                    positiveButtonText = context.getString(R.string.dialog_button_default_text_ok);
                }

                builder.setView(view);
                break;
            case INPUT:
                hasPositiveButton = true;
                hasNeutralButton = true;
                isKeyBoardAutoDisplay = true;

                view = View.inflate(context, R.layout.dialog_input, null);

                EditText editText = view.findViewById(R.id.inputMessage);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputLengthLimit)});
                if (inputMessageHint != null) editText.setHint(inputMessageHint);
                if (inputMessage != null) {
                    editText.setText(inputMessage);
                    editText.setSelectAllOnFocus(true);
                }
                editText.requestFocus();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        inputMessage = editable.toString();
                    }
                });

                if (message != null) {
                    TextView messageTextView = view.findViewById(R.id.message);
                    messageTextView.setText(message);
                }

                builder.setView(view);
                break;
            case SINGLE_CHOICE:
                hasPositiveButton = true;
                hasNeutralButton = true;

                if (list != null && list.length > 0) {
                    builder.setSingleChoiceItems(list, singleChoiceCheckedIndex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (list != null && which >= 0 && which < list.length) {
                                singleChoiceCheckedIndex = which;
                            } else Log.d("SINGLE_CHOICE with invalid click index : " + which);
                        }
                    });
                }
                break;
            case MULTI_CHOICE:
                hasPositiveButton = true;
                hasNeutralButton = true;

                if (list != null && list.length > 0) {
                    builder.setMultiChoiceItems(list, multiChoiceCheckedIndexList, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            if (multiChoiceCheckedIndexList != null && i >= 0 && i < multiChoiceCheckedIndexList.length) {
                                multiChoiceCheckedIndexList[i] = b;
                            } else Log.d("MULTI_CHOICE with invalid click index : " + i);
                        }
                    });
                }
                break;
            case DATE_PICK:
                //the flow is different with other dialogs
                DatePickerDialog datePickerDialog;

                //initial dialog & set listener
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        datePickYear = i;
                        datePickMonth = i1;
                        datePickDay = i2;

                        if (positiveListener != null) {
                            positiveListener.onClick();
                        }
                    }
                };

                if (style == DialogStyle.DEFAULT) {
                    datePickerDialog = new DatePickerDialog(context, onDateSetListener, datePickYear, datePickMonth, datePickDay);
                } else {
                    int styleResource = pickerDialogStyleResourceMap.get(style);
                    datePickerDialog = new DatePickerDialog(context, styleResource, onDateSetListener, datePickYear, datePickMonth, datePickDay);
                }

                //show dialog
                datePickerDialog.show();
                return;
            case TIME_PICK:
                //the flow is different with other dialogs
                TimePickerDialog timePickerDialog;

                //initial dialog & set listener
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        timePickHour = i;
                        timePickMinute = i1;

                        if (positiveListener != null) {
                            positiveListener.onClick();
                        }
                    }
                };

                if (style == DialogStyle.DEFAULT) {
                    timePickerDialog = new TimePickerDialog(context, onTimeSetListener, timePickHour, timePickMinute, is24HourView);
                } else {
                    int styleResource = pickerDialogStyleResourceMap.get(style);
                    timePickerDialog = new TimePickerDialog(context, styleResource, onTimeSetListener, timePickHour, timePickMinute, is24HourView);
                }

                //show dialog
                timePickerDialog.show();
                return;
            default:
                break;
        }

        //set button visibility & listener
        DialogInterface.OnClickListener tmpPositiveListener = null;
        DialogInterface.OnClickListener tmpNeutralListener = null;
        DialogInterface.OnClickListener tmpNegativeListener = null;
        if (positiveListener != null) {
            tmpPositiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    positiveListener.onClick();
                }
            };
        }
        if (neutralListener != null) {
            tmpNeutralListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    neutralListener.onClick();
                }
            };
        }
        if (negativeListener != null) {
            tmpNegativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    negativeListener.onClick();
                }
            };
        }
        if (hasPositiveButton) {
            if (positiveButtonText != null)
                builder.setPositiveButton(positiveButtonText, tmpPositiveListener);
            else
                builder.setPositiveButton(R.string.dialog_button_default_text_positive, tmpPositiveListener);
        }
        if (hasNeutralButton) {
            if (neutralButtonText != null)
                builder.setNeutralButton(neutralButtonText, tmpNeutralListener);
            else
                builder.setNeutralButton(R.string.dialog_button_default_text_neutral, tmpNeutralListener);
        }
        if (hasNegativeButton) {
            if (negativeButtonText != null)
                builder.setNegativeButton(negativeButtonText, tmpNegativeListener);
            else
                builder.setNegativeButton(R.string.dialog_button_default_text_negative, tmpNegativeListener);
        }

        //create dialog
        final AlertDialog dialog = builder.create();

        //set keyboard display
        if (isKeyBoardAutoDisplay && dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        //show dialog
        dialog.show();
    }

    public void setDialogStyle(DialogStyle style) {
        this.style = style;
    }

    public void setDialogClickListener(ListenerType type, ClickListener listener) {
        switch (type) {
            case POSITIVE:
                this.positiveListener = listener;
                break;
            case NEUTRAL:
                this.neutralListener = listener;
                break;
            case NEGATIVE:
                this.negativeListener = listener;
                break;
            case DATE_SET:
                this.positiveListener = listener;
                break;
            case TIME_SET:
                this.positiveListener = listener;
                break;
            default:
                Log.d("setDialogClickListener with invalid type: " + type);
                break;
        }
    }

    public void setArgument(DialogArgument name, boolean value) {
        booleanArguments.put(name, value);
    }

    public void setArgument(DialogArgument name, int value) {
        intArguments.put(name, value);
    }

    public void setArgument(DialogArgument name, String value) {
        stringArguments.put(name, value);
    }

    public void setArgument(DialogArgument name, Drawable value) {
        drawableArguments.put(name, value);
    }

    public void setArgument(DialogArgument name, boolean[] value) {
        booleanArrayArguments.put(name, value);
    }

    public void setArgument(DialogArgument name, String[] value) {
        stringArrayArguments.put(name, value);
    }

    @Nullable
    public Boolean getBooleanResult(DialogResult name) {
        String fieldName = resultFieldNameMap.get(name);

        try {
            return this.getClass().getDeclaredField(fieldName).getBoolean(this);
        } catch (IllegalAccessException e) {
            Log.printStackTrace(e);
            return null;
        } catch (NoSuchFieldException e) {
            Log.printStackTrace(e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.printStackTrace(e);
            return null;
        }
    }

    @Nullable
    public Integer getIntResult(DialogResult name) {
        String fieldName = resultFieldNameMap.get(name);

        try {
            return this.getClass().getDeclaredField(fieldName).getInt(this);
        } catch (IllegalAccessException e) {
            Log.printStackTrace(e);
            return null;
        } catch (NoSuchFieldException e) {
            Log.printStackTrace(e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.printStackTrace(e);
            return null;
        }
    }

    @Nullable
    public String getStringResult(DialogResult name) {
        String fieldName = resultFieldNameMap.get(name);

        try {
            return (String) this.getClass().getDeclaredField(fieldName).get(this);
        } catch (IllegalAccessException e) {
            Log.printStackTrace(e);
            return null;
        } catch (NoSuchFieldException e) {
            Log.printStackTrace(e);
            return null;
        } catch (ClassCastException e) {
            Log.printStackTrace(e);
            return null;
        }
    }

    @Nullable
    public int[] getIntArrayResult(DialogResult name) {
        String fieldName = resultFieldNameMap.get(name);

        try {
            return (int[]) this.getClass().getDeclaredField(fieldName).get(this);
        } catch (IllegalAccessException e) {
            Log.printStackTrace(e);
            return null;
        } catch (NoSuchFieldException e) {
            Log.printStackTrace(e);
            return null;
        } catch (ClassCastException e) {
            Log.printStackTrace(e);
            return null;
        }
    }

    public void clearArguments() {
        initialDialogByDefaultValues();
    }

    private void initialDialogStyleResourceMap() {
        dialogStyleResourceMap = new HashMap<>();

        dialogStyleResourceMap.put(DialogStyle.DARK_GRAY_BOTTOM, R.style.AlertDialogThemeDarkGray);

        //initial style map of picker
        pickerDialogStyleResourceMap = new HashMap<>();

        pickerDialogStyleResourceMap.put(DialogStyle.DARK_GRAY_BOTTOM, R.style.PickerDialogThemeDarkGray);
    }

    private void initialArgumentFieldNameMap() {
        argumentFieldNameMap = new HashMap<>();

        argumentFieldNameMap.put(DialogArgument.BOOLEAN_SKIP_HINT_CHECKED_STATE, "isHintSkipChecked");
        argumentFieldNameMap.put(DialogArgument.INT_CHECKED_INDEX_SINGLE_CHOICE, "singleChoiceCheckedIndex");
        argumentFieldNameMap.put(DialogArgument.STRING_DIALOG_TITLE, "title");
        argumentFieldNameMap.put(DialogArgument.STRING_DIALOG_MESSAGE, "message");
        argumentFieldNameMap.put(DialogArgument.STRING_DIALOG_MESSAGE_INPUT, "inputMessage");
        argumentFieldNameMap.put(DialogArgument.STRING_DIALOG_MESSAGE_INPUT_HINT, "inputMessageHint");
        argumentFieldNameMap.put(DialogArgument.STRING_DIALOG_MESSAGE_SKIP_HINT, "skipHintMessage");
        argumentFieldNameMap.put(DialogArgument.STRING_BUTTON_TEXT_POSITIVE, "positiveButtonText");
        argumentFieldNameMap.put(DialogArgument.STRING_BUTTON_TEXT_NEUTRAL, "neutralButtonText");
        argumentFieldNameMap.put(DialogArgument.STRING_BUTTON_TEXT_NEGATIVE, "negativeButtonText");
        argumentFieldNameMap.put(DialogArgument.DRAWABLE_DIALOG_ICON, "icon");
        argumentFieldNameMap.put(DialogArgument.BOOLEAN_ARRAY_CHECKED_STATE_MULTI_CHOICE, "multiChoiceCheckedIndexList");
        argumentFieldNameMap.put(DialogArgument.STRING_ARRAY_CHOICE_NAME, "list");
        argumentFieldNameMap.put(DialogArgument.INT_DAY_PICK_YEAR, "datePickYear");
        argumentFieldNameMap.put(DialogArgument.INT_DAY_PICK_MONTH, "datePickMonth");
        argumentFieldNameMap.put(DialogArgument.INT_DAY_PICK_DAY, "datePickDay");
        argumentFieldNameMap.put(DialogArgument.INT_TIME_PICK_HOUR, "timePickHour");
        argumentFieldNameMap.put(DialogArgument.INT_TIME_PICK_MINUTE, "timePickMinute");
        argumentFieldNameMap.put(DialogArgument.BOOLEAN_TIME_PICK_VIEW_24_HOUR, "is24HourView");
    }

    private void initialResultFieldNameMap() {
        resultFieldNameMap = new HashMap<>();

        resultFieldNameMap.put(DialogResult.BOOLEAN_CHECKED_STATE_SKIP_HINT, "isHintSkipChecked");
        resultFieldNameMap.put(DialogResult.INT_CHECKED_INDEX_SINGLE_CHOICE, "singleChoiceCheckedIndex");
        resultFieldNameMap.put(DialogResult.STRING_DIALOG_MESSAGE_INPUT, "inputMessage");
        resultFieldNameMap.put(DialogResult.BOOLEAN_ARRAY_CHECKED_STATE_MULTI_CHOICE, "multiChoiceCheckedIndexList");
        resultFieldNameMap.put(DialogResult.INT_DAY_PICK_YEAR, "datePickYear");
        resultFieldNameMap.put(DialogResult.INT_DAY_PICK_MONTH, "datePickMonth");
        resultFieldNameMap.put(DialogResult.INT_DAY_PICK_DAY, "datePickDay");
        resultFieldNameMap.put(DialogResult.INT_TIME_PICK_HOUR, "timePickHour");
        resultFieldNameMap.put(DialogResult.INT_TIME_PICK_MINUTE, "timePickMinute");
    }

    private void initialDialogByDefaultValues() {
        this.title = null;
        this.icon = null;
        this.message = null;
        this.list = null;
        this.positiveButtonText = null;
        this.neutralButtonText = null;
        this.negativeButtonText = null;
        this.skipHintMessage = null;
        this.singleChoiceCheckedIndex = DEFAULT_SINGLE_CHOICE_CHECKED_INDEX;
        this.isHintSkipChecked = DEFAULT_HINT_SKIP_CHECKED_STATE;
        this.inputLengthLimit = DEFAULT_INPUT_LENGTH_LIMIT;
        this.inputMessageHint = null;
        this.inputMessage = null;
        this.multiChoiceCheckedIndexList = null;
        this.booleanArguments = new HashMap<>();
        this.intArguments = new HashMap<>();
        this.stringArguments = new HashMap<>();
        this.drawableArguments = new HashMap<>();
        this.booleanArrayArguments = new HashMap<>();
        this.stringArrayArguments = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        this.datePickYear = calendar.get(Calendar.YEAR);
        this.datePickMonth = calendar.get(Calendar.MONTH);
        this.datePickDay = calendar.get(Calendar.DAY_OF_MONTH);
        this.timePickHour = calendar.get(Calendar.HOUR_OF_DAY);
        this.timePickMinute = calendar.get(Calendar.MINUTE);
        this.is24HourView = false;
    }

    private void applyArguments() {
        //apply boolean arguments
        for (Map.Entry<DialogArgument, Boolean> entry : booleanArguments.entrySet()) {
            DialogArgument key = entry.getKey();
            boolean value = entry.getValue();

            String fieldName = argumentFieldNameMap.get(key);
            try {
                this.getClass().getDeclaredField(fieldName).setBoolean(this, value);
            } catch (IllegalAccessException e) {
                Log.printStackTrace(e);
            } catch (NoSuchFieldException e) {
                Log.printStackTrace(e);
            } catch (IllegalArgumentException e) {
                Log.printStackTrace(e);
            }
        }

        //apply int arguments
        for (Map.Entry<DialogArgument, Integer> entry : intArguments.entrySet()) {
            DialogArgument key = entry.getKey();
            int value = entry.getValue();

            String fieldName = argumentFieldNameMap.get(key);
            try {
                this.getClass().getDeclaredField(fieldName).setInt(this, value);
            } catch (IllegalAccessException e) {
                Log.printStackTrace(e);
            } catch (NoSuchFieldException e) {
                Log.printStackTrace(e);
            } catch (IllegalArgumentException e) {
                Log.printStackTrace(e);
            }
        }

        //apply string arguments
        for (Map.Entry<DialogArgument, String> entry : stringArguments.entrySet()) {
            DialogArgument key = entry.getKey();
            String value = entry.getValue();

            String fieldName = argumentFieldNameMap.get(key);
            try {
                this.getClass().getDeclaredField(fieldName).set(this, value);
            } catch (IllegalAccessException e) {
                Log.printStackTrace(e);
            } catch (NoSuchFieldException e) {
                Log.printStackTrace(e);
            } catch (IllegalArgumentException e) {
                Log.printStackTrace(e);
            }
        }

        //apply drawable arguments
        for (Map.Entry<DialogArgument, Drawable> entry : drawableArguments.entrySet()) {
            DialogArgument key = entry.getKey();
            Drawable value = entry.getValue();

            String fieldName = argumentFieldNameMap.get(key);
            try {
                this.getClass().getDeclaredField(fieldName).set(this, value);
            } catch (IllegalAccessException e) {
                Log.printStackTrace(e);
            } catch (NoSuchFieldException e) {
                Log.printStackTrace(e);
            } catch (IllegalArgumentException e) {
                Log.printStackTrace(e);
            }
        }

        //apply boolean array arguments
        for (Map.Entry<DialogArgument, boolean[]> entry : booleanArrayArguments.entrySet()) {
            DialogArgument key = entry.getKey();
            boolean[] value = entry.getValue();

            String fieldName = argumentFieldNameMap.get(key);
            try {
                this.getClass().getDeclaredField(fieldName).set(this, value);
            } catch (IllegalAccessException e) {
                Log.printStackTrace(e);
            } catch (NoSuchFieldException e) {
                Log.printStackTrace(e);
            } catch (IllegalArgumentException e) {
                Log.printStackTrace(e);
            }
        }

        //apply string array arguments
        for (Map.Entry<DialogArgument, String[]> entry : stringArrayArguments.entrySet()) {
            DialogArgument key = entry.getKey();
            String[] value = entry.getValue();

            String fieldName = argumentFieldNameMap.get(key);
            try {
                this.getClass().getDeclaredField(fieldName).set(this, value);
            } catch (IllegalAccessException e) {
                Log.printStackTrace(e);
            } catch (NoSuchFieldException e) {
                Log.printStackTrace(e);
            } catch (IllegalArgumentException e) {
                Log.printStackTrace(e);
            }
        }
    }

    // TODO: not useful, need to be redesigned
}
