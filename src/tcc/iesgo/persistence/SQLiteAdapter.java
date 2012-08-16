package tcc.iesgo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SQLiteAdapter {

	public static final String MYDATABASE_NAME = "taxiandroid";
	public static final String MYDATABASE_TABLE = "user";
	public static final int MYDATABASE_VERSION = 1;
	public static final String KEY_ID = "_id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_LANG = "language";

	//Script de criação da tabela user
	private static final String SCRIPT_CREATE_DATABASE = "create table "
			+ MYDATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_USERNAME
			+ " text not null," + KEY_PASSWORD + " text not null," 
			+ KEY_LANG + " text not null);";

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	private Context context;

	public SQLiteAdapter(Context c) {
		context = c;
	}

	//Abre para leitura
	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,	MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}

	//Abre para escrita
	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,	MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}

	//Fecha a conexão
	public void close() {
		sqLiteHelper.close();
	}

	//Insere dados na tabela user
	public long insert(String content, String password, String lang) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_USERNAME, content);
		contentValues.put(KEY_PASSWORD, password);
		contentValues.put(KEY_LANG, lang);
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}

	//Deleta todos os registros da tabela
	public int deleteAll() {
		return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
	}

	//Retorna todos os registros da tabela
	public Cursor queueAll() {
		String[] columns = new String[] { KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_LANG };
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, null,
				null, null, null, null);
		return cursor;
	}

	public class SQLiteHelper extends SQLiteOpenHelper {
		
		public SQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_DATABASE); //Executa o script
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
	}
}