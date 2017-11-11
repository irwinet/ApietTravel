package application.android.irwinet.apiettravel;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.security.MessageDigest;
import java.util.Arrays;
import application.android.irwinet.apiettravel.FontManifest.TypefaceUtil;

/**
 * Created by Irwinet
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView etEmail;
    private EditText etPassword;
    private View mProgressView;
    private TextView tvRegister;
    private Button btnLogin,btnRegister;
    private LinearLayout email_login_form;

    /* *************************************
     *              FIREBASE               *
     ***************************************/
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    //End Firebase

    /* *************************************
     *              GOOGLE                 *
     ***************************************/
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    public static final int RC_GOOGLE_LOGIN = 1;
    //End Google

    /* *************************************
     *              FACEBOOK               *
     ***************************************/
    private CallbackManager mCallbackManager;
    private LoginButton mLoginButton;
    //End Facebook

    /* *************************************
     *              TWITTER                *
     ***************************************/
    TwitterLoginButton mTwitterLoginButton;
    //End Twitter

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        TwitterAuthConfig autoConfig = new TwitterAuthConfig(getString(R.string.apiKey),getString(R.string.apiSecret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this).twitterAuthConfig(autoConfig).build();
        Twitter.initialize(twitterConfig);

        TypefaceUtil.setDefaultFont(this, "DEFAULT", getString(R.string.pathPrimary));
        TypefaceUtil.setDefaultFont(this, "MONOSPACE", getString(R.string.pathPrimary));
        TypefaceUtil.setDefaultFont(this, "SERIF", getString(R.string.pathPrimary));
        TypefaceUtil.setDefaultFont(this, "SANS_SERIF", getString(R.string.pathPrimary));

        setContentView(R.layout.activity_login);

        //Assign Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize Controls
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (AutoCompleteTextView) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        mProgressView = findViewById(R.id.login_progress);
        email_login_form = (LinearLayout) findViewById(R.id.email_login_form);

        /* *************************************
         *              FACEBOOK               *
         ***************************************/
        mCallbackManager=CallbackManager.Factory.create();
        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("user_friends");
        //mLoginButton.setReadPermissions("public_profile");

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccesToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, getString(R.string.msgLogin), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, getString(R.string.msgLogin), Toast.LENGTH_SHORT).show();
            }
        });
        //End Login Facebook

        /* *************************************
         *               GOOGLE                *
         ***************************************/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .enableAutoManage(this,this)
                                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                                .build();
        mSignInButton = (SignInButton) findViewById(R.id.signInButton);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setColorScheme(SignInButton.COLOR_DARK);

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent,RC_GOOGLE_LOGIN);
            }
        });
        //End Login Google

        /* *************************************
         *                TWITTER              *
         ***************************************/
        mTwitterLoginButton = (TwitterLoginButton) findViewById(R.id.signInTwitter);
        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                signToFirebaseWithTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(LoginActivity.this, getString(R.string.msgLogin), Toast.LENGTH_SHORT).show();
            }
        });
        //End Login Twitter

        /* *************************************
         *                FIREBASE             *
         ***************************************/
        //Event Buttom Login
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                login(email,password);
            }
        });

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                add(email,password);
            }
        });
        //End Login Email And Password

        //Event Register
        tvRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRegister();
            }
        });

        //Event ActionBar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewWelcome();
            }
        });

        /* *************************************
         *                FIREBASE             *
         ***************************************/
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    viewMain();
                }
            }
        };
        //End Firebase

    }

    private void handleFacebookAccesToken(AccessToken accessToken)
    {
        mProgressView.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.GONE);

        AuthCredential credential=FacebookAuthProvider.getCredential(accessToken.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressView.setVisibility(View.GONE);
                mLoginButton.setVisibility(View.VISIBLE);

                if(!task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.msgLogin), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void signToFirebaseWithTwitterSession(TwitterSession session)
    {
        mProgressView.setVisibility(View.VISIBLE);
        mTwitterLoginButton.setVisibility(View.GONE);

        AuthCredential credential= TwitterAuthProvider.getCredential(session.getAuthToken().token,
                session.getAuthToken().secret);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressView.setVisibility(View.GONE);
                mTwitterLoginButton.setVisibility(View.VISIBLE);

                if(!task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.msgLogin), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //Error cuando cancela en login con gmail
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess())
        {
            firebaseAuthWithGoogle(result.getSignInAccount());
        }
        else
        {
            Toast.makeText(this, getString(R.string.msgLogin), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {

        mProgressView.setVisibility(View.VISIBLE);
        mSignInButton.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                mProgressView.setVisibility(View.GONE);
                mSignInButton.setVisibility(View.VISIBLE);

                if(!task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.msgLogin), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //End Facebook


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    //Facebook
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //End

    //Login With Email And Password
    public void add(String email, String password)
    {
        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        } else if(!isPasswordValid(password))
        {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            mProgressView.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgressView.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Usuario Creado Correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error Al Crear Usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void login(String email, String password)
    {
        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            mProgressView.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgressView.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        viewMain();
                    } else {
                        Toast.makeText(LoginActivity.this, "Usuario o Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    //End Login Email And Password

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_GOOGLE_LOGIN)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        mCallbackManager.onActivityResult(requestCode,resultCode,data);
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    //View Activity
    public void viewMain()
    {
        Intent intentMain=new Intent(this,MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentMain);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    public void viewWelcome()
    {
        Intent intentMain=new Intent(this,WelcomeActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentMain);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    public void viewRegister()
    {
        Intent intentRegister=new Intent(this,RegisterActivity.class);
        startActivity(intentRegister);
    }

    public String KeyHashes()
    {
        PackageInfo info;
        String KeyHashes=null;
        try
        {
            info = getPackageManager().getPackageInfo("application.android.irwinet.apiettravel",PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures)
            {
                MessageDigest md;
                md=MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                KeyHashes = new String(Base64.encode(md.digest(),0));
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return KeyHashes;
    }
}

