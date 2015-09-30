import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Storage {
	public static void write(ArrayList<AbstractTask> m) {
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<AbstractTask>>(){}.getType();
		String json = gson.toJson(m, type);
		try {
			FileWriter writer = new FileWriter("src/storage/storage.json");
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<AbstractTask> read() {
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<AbstractTask>>(){}.getType();
		try {
			FileInputStream in = new FileInputStream("src/storage/storage.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			ArrayList<AbstractTask> m = gson.fromJson(reader, type);
			reader.close();
			return m;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}