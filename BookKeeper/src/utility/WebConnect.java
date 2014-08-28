package utility;

import java.io.IOException;
import java.io.InputStream;

import com.bookies.bookkeeper.Variables;
import com.madmarcos.resttest.*;

import android.util.Log;

public class WebConnect {
	private static final String DEFAULT_LOGIN = "cs4953s14t3user";
	private static final String DEFAULT_PW = "gK23yp4D@5jwX62b226eS";
	
	public void fetch() {
		/*Variables.setRest(new RestFetcher());
		try {
			InputStream caInput = getAssets().open("fulgentcorp.crt");
			Variables.getRest().initKeyStore(caInput);
			caInput.close();
		} catch (IOException e) {
			Log.e("WebConnect", "*** error in fetch: " + e.getMessage());
		}

		new LoginTask(Variables.getWS_URL(), DEFAULT_LOGIN, DEFAULT_PW, new LoginCallback(), Variables.getRest()).execute();
		*/
	}
	
	private void setSessionInfo(int i, String s) {
		Variables.setSessionId(i);
		Variables.setSalt(s);
	}
}



