package es.softwhisper.airhockey.renderers;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import es.softwhisper.airhockey.R;
import es.softwhisper.airhockey.util.LoggerConfig;
import es.softwhisper.airhockey.util.ShaderHelper;
import es.softwhisper.airhockey.util.TextResourceReader;

/**
 * 
 * @author pablo
 *
 */
public class AirHockeyRenderer implements Renderer {
	
	float [] tableTrianglesVertices = { -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,  	// first triangle
										-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,   	// second triangle 
										-0.5f, 0f, 0.5f, 0f, 			// center line
										0f, -0.25f, 					// mallet1  
										0f, 0.25f }; 				// mallet2 
	
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private final FloatBuffer vertexData;
	
	private static final String A_POSITION = "a_Position";
	private static final String[] ATTRIBUTES = { A_POSITION };
	private static final int A_POSITION_LOCATION = Arrays.asList(ATTRIBUTES).indexOf(A_POSITION);
	
	private static final String U_COLOR = "u_Color";
	private int uColorLocation;
	 
	private int program;
	
	private final Context context;
	
	public AirHockeyRenderer(Context context) {
		this.context = context;
		
		vertexData = ByteBuffer.allocateDirect(tableTrianglesVertices.length * BYTES_PER_FLOAT)
								.order(ByteOrder.nativeOrder())
								.asFloatBuffer();
		
		vertexData.put(tableTrianglesVertices);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);
		
		// Draw the table 
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		
		// Draw the diving line
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 0.0f);
		glDrawArrays(GL_LINES, 6, 2);
		
		// Draw the first mallet
		glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 8, 1);
		
		// Draw the second mallet
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_POINTS, 9, 1);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader, ATTRIBUTES);
		
		if (LoggerConfig.ON) ShaderHelper.validateProgram(program);
		
		glUseProgram(program);
		
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		
		vertexData.position(0);
		glVertexAttribPointer(A_POSITION_LOCATION, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		glEnableVertexAttribArray(A_POSITION_LOCATION);
		
	}

}
