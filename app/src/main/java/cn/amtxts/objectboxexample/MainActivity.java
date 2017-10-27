package cn.amtxts.objectboxexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

public class MainActivity extends AppCompatActivity {

    private EditText etText;
    private ListView lvContent;
    private Box<Note> notesBox;
    private List<Note> mNotes;
    private CommonAdapter mAdapter;
    private Query<Note> notesQuery;
    private Note noteChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BoxStore boxStore = ((App) getApplication()).getBoxStore();
        notesBox = boxStore.boxFor(Note.class);
        notesQuery = notesBox.query().order(Note_.text).build();
        mNotes = new ArrayList<>();
        updateDatabase();

        setUpView();

    }

    private void updateDatabase() {
        mNotes.clear();
        mNotes.addAll(notesQuery.find());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void setUpView() {
        etText = (EditText) findViewById(R.id.etText);
        lvContent = (ListView) findViewById(R.id.lvContent);
        mAdapter = new CommonAdapter<Note>(this, R.layout.item_list, mNotes) {
            @Override
            protected void convert(ViewHolder viewHolder, Note item, int position) {
                viewHolder.setText(R.id.tvContent, item.getText());
                viewHolder.setText(R.id.tvDate, item.getComment());
            }
        };
        lvContent.setAdapter(mAdapter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                noteChange = mNotes.get(i);
                etText.setText(noteChange.getText());
            }
        });
    }

    public void toAdd(View view) {
        String noteText = etText.getText().toString();
        etText.setText("");

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        Note note = new Note();
        note.setText(noteText);
        note.setComment(comment);
        note.setDate(new Date());
        notesBox.put(note);
        updateDatabase();
    }

    public void toDel(View view) {
        if (noteChange != null) {
            etText.setText("");
            notesBox.remove(noteChange);
            updateDatabase();
        }
    }

    public void toUpdate(View view) {
        String noteText = etText.getText().toString();
        if (noteChange != null && !TextUtils.isEmpty(noteText)) {
            etText.setText("");
            noteChange.setText(noteText);
            notesBox.put(noteChange);
            updateDatabase();
        }
    }

    public void toQuery(View view) {

    }
}
