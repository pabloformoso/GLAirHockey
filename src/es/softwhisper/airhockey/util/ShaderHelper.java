package es.softwhisper.airhockey.util;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindAttribLocation;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;
import android.util.Log;

/**
 * ShaderHelper
 * 
 * @author pablo
 *
 */
public class ShaderHelper {
	private static final String TAG = "ShaderHelper";
	
	/**
	 * 
	 * @param shaderCode
	 * @return result of compiling
	 */
	public static int compileVertexShader(String shaderCode) {
		return compileShader(GL_VERTEX_SHADER, shaderCode);
	}
	
	/**
	 * 
	 * @param shaderCode
	 * @return result of compiling
	 */
	public static int compileFragmentShader(String shaderCode) {
		return compileShader(GL_FRAGMENT_SHADER, shaderCode);
	}
	
	/**
	 * Links vertex and fragment to render the shader
	 * 
	 * @param vertexShaderId
	 * @param fragmentShaderId
	 * @param attributes
	 * @return program id if success - 0 if fails
	 */
	public static int linkProgram(int vertexShaderId, int fragmentShaderId, String[] attributes) {
		
		final int programObjectId = glCreateProgram();
		
		if (programObjectId == 0) {
			if (LoggerConfig.ON) Log.w(TAG, "Could not create new program");
			return 0;
		}
		
		glAttachShader(programObjectId, vertexShaderId);
		glAttachShader(programObjectId, fragmentShaderId);
		
		for (int a = 0; a < attributes.length; a++) {
			glBindAttribLocation(programObjectId, a, attributes[a]);
		}
		
		glLinkProgram(programObjectId);
		
		final int[] linkStatus = new int[1];
		glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
		
		if (LoggerConfig.ON) Log.v(TAG, "Results: \n" + glGetProgramInfoLog(programObjectId) + "\n: " + programObjectId);
		
		if (linkStatus[0] == 0) {
			glDeleteProgram(programObjectId);
			
			if (LoggerConfig.ON) Log.w(TAG, "Linking program failed.");
			
			return 0;
		}
		
		return programObjectId;
	}
	
	/**
	 * validates the compiled and linked a GLProgram
	 * 
	 * @param programId
	 * @return true if program can be run
	 */
	public static boolean validateProgram(int programId) {
		glValidateProgram(programId);
		
		final int[] validateStatus = new int[1];
		
		glGetProgramiv(programId, GL_VALIDATE_STATUS, validateStatus, 0);
		
		Log.v(TAG,	"Validaton status: " + validateStatus[0] + "\nLog: " + glGetProgramInfoLog(programId));
		
		return validateStatus[0] != 0;
	}
	
	/*
	 * Compiles the code for the given type.
	 * 
	 * @param type
	 * @param shaderCode
	 * @return result of compiling
	 */
	private static int compileShader(int type, String shaderCode) {
		
		final int shaderObjectId = glCreateShader(type);
		
		if (shaderObjectId == 0) {
			if (LoggerConfig.ON) Log.w(TAG, "Could not create the shader");
			return 0;
		}
			
		glShaderSource(shaderObjectId, shaderCode);
		glCompileShader(shaderObjectId);
		
		final int [] compilationStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compilationStatus, 0);
		
		if (LoggerConfig.ON) Log.v(TAG, "Results of compiling: \n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
		
		if (compilationStatus[0] == 0) {
			glDeleteShader(shaderObjectId);
			if (LoggerConfig.ON) Log.w(TAG, "Compilation failed.");
			return 0;
		}
		
		return shaderObjectId;
	}
}
