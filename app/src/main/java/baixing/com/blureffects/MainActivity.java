package baixing.com.blureffects;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.enrique.stackblur.StackBlurManager;

public class MainActivity extends AppCompatActivity {

	private static final int RESULT_LOAD_IMAGE = 1001;
	private StackBlurManager _stackBlurManager;
	private SeekBar _seekBar;
	private TextView _seekTextView;
	private int type;
	private int it;
	private Bitmap bitmap;

	private int centerX;
	private int centerY;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_stackBlurManager = new StackBlurManager(this,R.drawable.teste);
		_seekBar= (SeekBar)(findViewById(R.id.blur_seekBar));
		_seekTextView= (TextView) findViewById(R.id.blur_amount);
		_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
			                              boolean fromUser) {
				_seekTextView.setText(""+(progress+1));
				onBlur(_seekBar.getProgress()+1);
			}
		});


		((ImageView)findViewById(R.id.image)).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				Log.v("tinglog", motionEvent.getX() * 100 / view.getWidth() + " " + motionEvent.getY() * 100 / view.getHeight());
				centerX = (int) (motionEvent.getX() * 100 / view.getWidth());
				centerY = (int) (motionEvent.getY() * 100 / view.getHeight());
				if(type==R.id.button_radial){
					onBlur(_seekBar.getProgress()+1);
				}
				return false;
			}
		});

	}

	private void onBlur(int radius) {
		;
		bitmap=progressing(radius);
		((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);
	}
	public void changeProgress(View v){
		type=v.getId();
		if(bitmap!=null)
			((ImageView)findViewById(R.id.image_before)).setImageBitmap(bitmap);
	}
	public void changeIt(View v){
		if(v.getId()==R.id.button_add){
			it++;

		}else{
			if(it>0)
				it--;

		}
		((TextView)findViewById(R.id.it)).setText(it + "");
	}
	public void changeImage(View v){
		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			bitmap=(BitmapFactory.decodeFile(picturePath));
			((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);
			_stackBlurManager.setBitmap(bitmap);
		}

	}
	private Bitmap progressing(int radius){
		switch (type){
			case R.id.button_c:
				return  _stackBlurManager.processNatively(radius);
			case R.id.button_j:
				return  _stackBlurManager.process(radius);
			case R.id.button_radial:
				return  _stackBlurManager.processRadial(radius,centerX,centerY);
			case R.id.button_circular:
				return  _stackBlurManager.processCircular(radius);
			case R.id.button_h:
				return  _stackBlurManager.processHorizontal(radius);
			case R.id.button_v:
				return  _stackBlurManager.processVertical(radius);
			case R.id.button_g:
				return  _stackBlurManager.processBoxNatively(radius);
		}
		return  _stackBlurManager.processNatively(radius);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
