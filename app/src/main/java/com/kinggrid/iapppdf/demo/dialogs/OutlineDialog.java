package com.kinggrid.iapppdf.demo.dialogs;

import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kinggrid.iapppdf.core.codec.OutlineLink;
import com.kinggrid.iapppdf.demo.apdater.OutlineAdapter;
import com.kinggrid.iapppdf.emdev.utils.LayoutUtils;
import com.kinggrid.iapppdf.ui.viewer.IAppPDFActivity;
import com.cec.szoa.R;

public class OutlineDialog extends Dialog implements OnItemClickListener {

    final IAppPDFActivity base;
    final List<OutlineLink> outline;


    public OutlineDialog(final IAppPDFActivity base, final List<OutlineLink> outline) {
        super(base);
        this.base = base;
        this.outline = outline;

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutUtils.maximizeWindow(getWindow());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.outline_layout);
        final ListView listView = (ListView) findViewById(R.id.outline_list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

//        setContentView(listView);

//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.list_title);

        final Button btn_outline_close = (Button) findViewById(R.id.title_btn_close);
        btn_outline_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });
        
        final TextView tv_outline_title = (TextView) findViewById(R.id.list_title);
        tv_outline_title.setText(R.string.outline_title);

        OutlineLink current = base.getCurrentOutline();
        final OutlineAdapter adapter = new OutlineAdapter(getContext(), base, outline, current);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (current != null) {
            final int pos = adapter.getItemPosition(current);
            if (pos != -1) {
                listView.setSelection(pos);
            }
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        this.dismiss();
        base.gotoOutlineItem((OutlineLink) parent.getAdapter().getItem(position));
    }
}
