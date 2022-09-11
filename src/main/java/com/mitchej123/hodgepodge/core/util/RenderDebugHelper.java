package com.mitchej123.hodgepodge.core.util;

import static org.lwjgl.opengl.ARBImaging.*;
import static org.lwjgl.opengl.ARBImaging.GL_POST_COLOR_MATRIX_COLOR_TABLE;
import static org.lwjgl.opengl.ARBImaging.GL_POST_CONVOLUTION_COLOR_TABLE;
import static org.lwjgl.opengl.ARBImaging.GL_SEPARABLE_2D;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_FUNC;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL14.GL_BLEND_SRC_RGB;
import static org.lwjgl.opengl.GL15.GL_FOG_COORD_ARRAY;
import static org.lwjgl.opengl.GL20.GL_POINT_SPRITE;
import static org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_TWO_SIDE;

import com.google.common.collect.ImmutableMap;
import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import com.mitchej123.hodgepodge.core.HodgePodgeClient.RenderDebugMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
// spotless:off
// @formatter:off
// this file is formatted in such a way that makes block/sed/other line based editing easy.
// formatter will usually screw this up by introducing line breaks when it uses some very long GL constants, or I placed
// too much content (maybe even multiple statements), so we will turn them off here.

public class RenderDebugHelper {
    public static final Logger log = LogManager.getLogger("RenderDebug");
    /*
    Reduced states considers only these states

    GL_BLEND
    GL_ALPHA_TEST
    GL_CULL_FACE
    GL_DEPTH_TEST
    GL_RESCALE_NORMAL
    GL_DEPTH_FUNC
    */
    // boolean states
    private static boolean saved_GL_ALPHA_TEST;
    private static boolean saved_GL_AUTO_NORMAL;
    private static boolean saved_GL_BLEND;
    private static boolean saved_GL_COLOR_ARRAY;
    private static boolean saved_GL_COLOR_LOGIC_OP;
    private static boolean saved_GL_COLOR_MATERIAL;
    private static boolean saved_GL_COLOR_SUM;
    private static boolean saved_GL_COLOR_TABLE;
    private static boolean saved_GL_CONVOLUTION_1D;
    private static boolean saved_GL_CONVOLUTION_2D;
    private static boolean saved_GL_CULL_FACE;
    private static boolean saved_GL_DEPTH_TEST;
    private static boolean saved_GL_DITHER;
    private static boolean saved_GL_EDGE_FLAG_ARRAY;
    private static boolean saved_GL_FOG;
    private static boolean saved_GL_FOG_COORD_ARRAY;
    private static boolean saved_GL_HISTOGRAM;
    private static boolean saved_GL_INDEX_ARRAY;
    private static boolean saved_GL_INDEX_LOGIC_OP;
    private static boolean saved_GL_LIGHTING;
    private static boolean saved_GL_LINE_SMOOTH;
    private static boolean saved_GL_LINE_STIPPLE;
    private static boolean saved_GL_MAP1_COLOR_4;
    private static boolean saved_GL_MAP1_INDEX;
    private static boolean saved_GL_MAP1_NORMAL;
    private static boolean saved_GL_MAP1_TEXTURE_COORD_1;
    private static boolean saved_GL_MAP1_TEXTURE_COORD_2;
    private static boolean saved_GL_MAP1_TEXTURE_COORD_3;
    private static boolean saved_GL_MAP1_TEXTURE_COORD_4;
    private static boolean saved_GL_MAP2_COLOR_4;
    private static boolean saved_GL_MAP2_INDEX;
    private static boolean saved_GL_MAP2_NORMAL;
    private static boolean saved_GL_MAP2_TEXTURE_COORD_1;
    private static boolean saved_GL_MAP2_TEXTURE_COORD_2;
    private static boolean saved_GL_MAP2_TEXTURE_COORD_3;
    private static boolean saved_GL_MAP2_TEXTURE_COORD_4;
    private static boolean saved_GL_MAP2_VERTEX_3;
    private static boolean saved_GL_MAP2_VERTEX_4;
    private static boolean saved_GL_MINMAX;
    private static boolean saved_GL_MULTISAMPLE;
    private static boolean saved_GL_NORMAL_ARRAY;
    private static boolean saved_GL_NORMALIZE;
    private static boolean saved_GL_POINT_SMOOTH;
    private static boolean saved_GL_POINT_SPRITE;
    private static boolean saved_GL_POLYGON_SMOOTH;
    private static boolean saved_GL_POLYGON_OFFSET_FILL;
    private static boolean saved_GL_POLYGON_OFFSET_LINE;
    private static boolean saved_GL_POLYGON_OFFSET_POINT;
    private static boolean saved_GL_POLYGON_STIPPLE;
    private static boolean saved_GL_POST_COLOR_MATRIX_COLOR_TABLE;
    private static boolean saved_GL_POST_CONVOLUTION_COLOR_TABLE;
    private static boolean saved_GL_RESCALE_NORMAL;
    private static boolean saved_GL_SAMPLE_ALPHA_TO_COVERAGE;
    private static boolean saved_GL_SAMPLE_ALPHA_TO_ONE;
    private static boolean saved_GL_SAMPLE_COVERAGE;
    private static boolean saved_GL_SCISSOR_TEST;
    private static boolean saved_GL_SECONDARY_COLOR_ARRAY;
    private static boolean saved_GL_SEPARABLE_2D;
    private static boolean saved_GL_STENCIL_TEST;
    private static boolean saved_GL_TEXTURE_1D;
    private static boolean saved_GL_TEXTURE_2D;
    private static boolean saved_GL_TEXTURE_3D;
    private static boolean saved_GL_TEXTURE_COORD_ARRAY;
    private static boolean saved_GL_TEXTURE_CUBE_MAP;
    private static boolean saved_GL_TEXTURE_GEN_Q;
    private static boolean saved_GL_TEXTURE_GEN_R;
    private static boolean saved_GL_TEXTURE_GEN_S;
    private static boolean saved_GL_TEXTURE_GEN_T;
    private static boolean saved_GL_VERTEX_ARRAY;
    private static boolean saved_GL_VERTEX_PROGRAM_POINT_SIZE;
    private static boolean saved_GL_VERTEX_PROGRAM_TWO_SIDE;
    // int states
    private static int saved_GL_ALPHA_TEST_FUNC;
    private static int saved_GL_DEPTH_FUNC;
    private static int saved_GL_BLEND_DST_ALPHA;
    private static int saved_GL_BLEND_DST_RGB;
    private static int saved_GL_BLEND_SRC_ALPHA;
    private static int saved_GL_BLEND_SRC_RGB;
    // double states
    private static float saved_GL_ALPHA_TEST_REF;

    private static final Map<Integer, String> functions = ImmutableMap.<Integer, String>builder()
            .put(GL11.GL_NEVER, "GL_NEVER")
            .put(GL11.GL_LESS, "GL_LESS")
            .put(GL11.GL_EQUAL, "GL_EQUAL")
            .put(GL11.GL_LEQUAL, "GL_LEQUAL")
            .put(GL11.GL_GREATER, "GL_GREATER")
            .put(GL11.GL_NOTEQUAL, "GL_NOTEQUAL")
            .put(GL11.GL_GEQUAL, "GL_GEQUAL")
            .put(GL11.GL_ALWAYS, "GL_ALWAYS")
            .put(GL11.GL_ZERO, "GL_ZERO")
            .put(GL11.GL_ONE, "GL_ONE")
            .put(GL11.GL_SRC_COLOR, "GL_SRC_COLOR")
            .put(GL11.GL_ONE_MINUS_SRC_COLOR, "GL_ONE_MINUS_SRC_COLOR")
            .put(GL11.GL_DST_COLOR, "GL_DST_COLOR")
            .put(GL11.GL_ONE_MINUS_DST_COLOR, "GL_ONE_MINUS_DST_COLOR")
            .put(GL11.GL_SRC_ALPHA, "GL_SRC_ALPHA")
            .put(GL11.GL_ONE_MINUS_SRC_ALPHA, "GL_ONE_MINUS_SRC_ALPHA")
            .put(GL11.GL_DST_ALPHA, "GL_DST_ALPHA")
            .put(GL11.GL_ONE_MINUS_DST_ALPHA, "GL_ONE_MINUS_DST_ALPHA")
            .put(GL11.GL_CONSTANT_COLOR, "GL_CONSTANT_COLOR")
            .put(GL11.GL_ONE_MINUS_CONSTANT_COLOR, "GL_ONE_MINUS_CONSTANT_COLOR")
            .put(GL11.GL_CONSTANT_ALPHA, "GL_CONSTANT_ALPHA")
            .put(GL11.GL_ONE_MINUS_CONSTANT_ALPHA, "GL_ONE_MINUS_CONSTANT_ALPHA")
            .put(GL11.GL_SRC_ALPHA_SATURATE, "GL_SRC_ALPHA_SATURATE")
            .build();

    private static final List<String> errorBuffer = new ArrayList<>(32);

    public static void recordGLStates() {
        if (HodgePodgeClient.renderDebugMode.is(RenderDebugMode.OFF))
            return;
        if (HodgePodgeClient.renderDebugMode.is(RenderDebugMode.REDUCED)) {
            saved_GL_BLEND = GL11.glGetBoolean(GL_BLEND);
            saved_GL_ALPHA_TEST = GL11.glGetBoolean(GL_ALPHA_TEST);
            saved_GL_CULL_FACE = GL11.glGetBoolean(GL_CULL_FACE);
            saved_GL_DEPTH_TEST = GL11.glGetBoolean(GL_DEPTH_TEST);
            saved_GL_RESCALE_NORMAL = GL11.glGetBoolean(GL_RESCALE_NORMAL);
            saved_GL_DEPTH_FUNC = GL11.glGetInteger(GL_DEPTH_FUNC);
            return;
        }

        saved_GL_ALPHA_TEST = GL11.glIsEnabled(GL_ALPHA_TEST);
        saved_GL_AUTO_NORMAL = GL11.glIsEnabled(GL_AUTO_NORMAL);
        saved_GL_BLEND = GL11.glIsEnabled(GL_BLEND);
        saved_GL_COLOR_ARRAY = GL11.glIsEnabled(GL_COLOR_ARRAY);
        saved_GL_COLOR_LOGIC_OP = GL11.glIsEnabled(GL_COLOR_LOGIC_OP);
        saved_GL_COLOR_MATERIAL = GL11.glIsEnabled(GL_COLOR_MATERIAL);
        saved_GL_COLOR_SUM = GL11.glIsEnabled(GL_COLOR_SUM);
        saved_GL_COLOR_TABLE = GL11.glIsEnabled(GL_COLOR_TABLE);
        saved_GL_CONVOLUTION_1D = GL11.glIsEnabled(GL_CONVOLUTION_1D);
        saved_GL_CONVOLUTION_2D = GL11.glIsEnabled(GL_CONVOLUTION_2D);
        saved_GL_CULL_FACE = GL11.glIsEnabled(GL_CULL_FACE);
        saved_GL_DEPTH_TEST = GL11.glIsEnabled(GL_DEPTH_TEST);
        saved_GL_DITHER = GL11.glIsEnabled(GL_DITHER);
        saved_GL_EDGE_FLAG_ARRAY = GL11.glIsEnabled(GL_EDGE_FLAG_ARRAY);
        saved_GL_FOG = GL11.glIsEnabled(GL_FOG);
        saved_GL_FOG_COORD_ARRAY = GL11.glIsEnabled(GL_FOG_COORD_ARRAY);
        saved_GL_HISTOGRAM = GL11.glIsEnabled(GL_HISTOGRAM);
        saved_GL_INDEX_ARRAY = GL11.glIsEnabled(GL_INDEX_ARRAY);
        saved_GL_INDEX_LOGIC_OP = GL11.glIsEnabled(GL_INDEX_LOGIC_OP);
        saved_GL_LIGHTING = GL11.glIsEnabled(GL_LIGHTING);
        saved_GL_LINE_SMOOTH = GL11.glIsEnabled(GL_LINE_SMOOTH);
        saved_GL_LINE_STIPPLE = GL11.glIsEnabled(GL_LINE_STIPPLE);
        saved_GL_MAP1_COLOR_4 = GL11.glIsEnabled(GL_MAP1_COLOR_4);
        saved_GL_MAP1_INDEX = GL11.glIsEnabled(GL_MAP1_INDEX);
        saved_GL_MAP1_NORMAL = GL11.glIsEnabled(GL_MAP1_NORMAL);
        saved_GL_MAP1_TEXTURE_COORD_1 = GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_1);
        saved_GL_MAP1_TEXTURE_COORD_2 = GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_2);
        saved_GL_MAP1_TEXTURE_COORD_3 = GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_3);
        saved_GL_MAP1_TEXTURE_COORD_4 = GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_4);
        saved_GL_MAP2_COLOR_4 = GL11.glIsEnabled(GL_MAP2_COLOR_4);
        saved_GL_MAP2_INDEX = GL11.glIsEnabled(GL_MAP2_INDEX);
        saved_GL_MAP2_NORMAL = GL11.glIsEnabled(GL_MAP2_NORMAL);
        saved_GL_MAP2_TEXTURE_COORD_1 = GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_1);
        saved_GL_MAP2_TEXTURE_COORD_2 = GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_2);
        saved_GL_MAP2_TEXTURE_COORD_3 = GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_3);
        saved_GL_MAP2_TEXTURE_COORD_4 = GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_4);
        saved_GL_MAP2_VERTEX_3 = GL11.glIsEnabled(GL_MAP2_VERTEX_3);
        saved_GL_MAP2_VERTEX_4 = GL11.glIsEnabled(GL_MAP2_VERTEX_4);
        saved_GL_MINMAX = GL11.glIsEnabled(GL_MINMAX);
        saved_GL_MULTISAMPLE = GL11.glIsEnabled(GL_MULTISAMPLE);
        saved_GL_NORMAL_ARRAY = GL11.glIsEnabled(GL_NORMAL_ARRAY);
        saved_GL_NORMALIZE = GL11.glIsEnabled(GL_NORMALIZE);
        saved_GL_POINT_SMOOTH = GL11.glIsEnabled(GL_POINT_SMOOTH);
        saved_GL_POINT_SPRITE = GL11.glIsEnabled(GL_POINT_SPRITE);
        saved_GL_POLYGON_SMOOTH = GL11.glIsEnabled(GL_POLYGON_SMOOTH);
        saved_GL_POLYGON_OFFSET_FILL = GL11.glIsEnabled(GL_POLYGON_OFFSET_FILL);
        saved_GL_POLYGON_OFFSET_LINE = GL11.glIsEnabled(GL_POLYGON_OFFSET_LINE);
        saved_GL_POLYGON_OFFSET_POINT = GL11.glIsEnabled(GL_POLYGON_OFFSET_POINT);
        saved_GL_POLYGON_STIPPLE = GL11.glIsEnabled(GL_POLYGON_STIPPLE);
        saved_GL_POST_COLOR_MATRIX_COLOR_TABLE = GL11.glIsEnabled(GL_POST_COLOR_MATRIX_COLOR_TABLE);
        saved_GL_POST_CONVOLUTION_COLOR_TABLE = GL11.glIsEnabled(GL_POST_CONVOLUTION_COLOR_TABLE);
        saved_GL_RESCALE_NORMAL = GL11.glIsEnabled(GL_RESCALE_NORMAL);
        saved_GL_SAMPLE_ALPHA_TO_COVERAGE = GL11.glIsEnabled(GL_SAMPLE_ALPHA_TO_COVERAGE);
        saved_GL_SAMPLE_ALPHA_TO_ONE = GL11.glIsEnabled(GL_SAMPLE_ALPHA_TO_ONE);
        saved_GL_SAMPLE_COVERAGE = GL11.glIsEnabled(GL_SAMPLE_COVERAGE);
        saved_GL_SCISSOR_TEST = GL11.glIsEnabled(GL_SCISSOR_TEST);
        saved_GL_SECONDARY_COLOR_ARRAY = GL11.glIsEnabled(GL_SECONDARY_COLOR_ARRAY);
        saved_GL_SEPARABLE_2D = GL11.glIsEnabled(GL_SEPARABLE_2D);
        saved_GL_STENCIL_TEST = GL11.glIsEnabled(GL_STENCIL_TEST);
        saved_GL_TEXTURE_1D = GL11.glIsEnabled(GL_TEXTURE_1D);
        saved_GL_TEXTURE_2D = GL11.glIsEnabled(GL_TEXTURE_2D);
        saved_GL_TEXTURE_3D = GL11.glIsEnabled(GL_TEXTURE_3D);
        saved_GL_TEXTURE_COORD_ARRAY = GL11.glIsEnabled(GL_TEXTURE_COORD_ARRAY);
        saved_GL_TEXTURE_CUBE_MAP = GL11.glIsEnabled(GL_TEXTURE_CUBE_MAP);
        saved_GL_TEXTURE_GEN_Q = GL11.glIsEnabled(GL_TEXTURE_GEN_Q);
        saved_GL_TEXTURE_GEN_R = GL11.glIsEnabled(GL_TEXTURE_GEN_R);
        saved_GL_TEXTURE_GEN_S = GL11.glIsEnabled(GL_TEXTURE_GEN_S);
        saved_GL_TEXTURE_GEN_T = GL11.glIsEnabled(GL_TEXTURE_GEN_T);
        saved_GL_VERTEX_ARRAY = GL11.glIsEnabled(GL_VERTEX_ARRAY);
        saved_GL_VERTEX_PROGRAM_POINT_SIZE = GL11.glIsEnabled(GL_VERTEX_PROGRAM_POINT_SIZE);
        saved_GL_VERTEX_PROGRAM_TWO_SIDE = GL11.glIsEnabled(GL_VERTEX_PROGRAM_TWO_SIDE);
        saved_GL_ALPHA_TEST_FUNC = GL11.glGetInteger(GL_ALPHA_TEST_FUNC);
        saved_GL_ALPHA_TEST_REF = GL11.glGetFloat(GL_ALPHA_TEST_REF);
        saved_GL_DEPTH_FUNC = GL11.glGetInteger(GL_DEPTH_FUNC);
        saved_GL_BLEND_DST_ALPHA = GL11.glGetInteger(GL_BLEND_DST_ALPHA);
        saved_GL_BLEND_DST_RGB = GL11.glGetInteger(GL_BLEND_DST_RGB);
        saved_GL_BLEND_SRC_ALPHA = GL11.glGetInteger(GL_BLEND_SRC_ALPHA);
        saved_GL_BLEND_SRC_RGB = GL11.glGetInteger(GL_BLEND_SRC_RGB);
    }

    /**
     *
     * @return true if gl states is same as recorded
     */
    public static boolean checkGLStates() {
        if (HodgePodgeClient.renderDebugMode.is(RenderDebugMode.OFF))
            return true;

        errorBuffer.clear();
        if (HodgePodgeClient.renderDebugMode.is(RenderDebugMode.REDUCED)) {
            if (GL11.glGetBoolean(GL_BLEND) != saved_GL_BLEND) errorBuffer.add("GL_BLEND");
            if (GL11.glGetBoolean(GL_ALPHA_TEST) != saved_GL_ALPHA_TEST) errorBuffer.add("GL_ALPHA_TEST");
            if (GL11.glGetBoolean(GL_CULL_FACE) != saved_GL_CULL_FACE) errorBuffer.add("GL_CULL_FACE");
            if (GL11.glGetBoolean(GL_DEPTH_TEST) != saved_GL_DEPTH_TEST) errorBuffer.add("GL_DEPTH_TEST");
            if (GL11.glGetBoolean(GL_RESCALE_NORMAL) != saved_GL_RESCALE_NORMAL) errorBuffer.add("GL_RESCALE_NORMAL");
            if (GL11.glGetInteger(GL_DEPTH_FUNC) != saved_GL_DEPTH_FUNC) errorBuffer.add("GL_DEPTH_FUNC");

            return errorBuffer.isEmpty();
        }

        if (GL11.glIsEnabled(GL_ALPHA_TEST) != saved_GL_ALPHA_TEST) errorBuffer.add("GL_ALPHA_TEST");
        if (GL11.glIsEnabled(GL_AUTO_NORMAL) != saved_GL_AUTO_NORMAL) errorBuffer.add("GL_AUTO_NORMAL");
        if (GL11.glIsEnabled(GL_BLEND) != saved_GL_BLEND) errorBuffer.add("GL_BLEND");
        if (GL11.glIsEnabled(GL_COLOR_ARRAY) != saved_GL_COLOR_ARRAY) errorBuffer.add("GL_COLOR_ARRAY");
        if (GL11.glIsEnabled(GL_COLOR_LOGIC_OP) != saved_GL_COLOR_LOGIC_OP) errorBuffer.add("GL_COLOR_LOGIC_OP");
        if (GL11.glIsEnabled(GL_COLOR_MATERIAL) != saved_GL_COLOR_MATERIAL) errorBuffer.add("GL_COLOR_MATERIAL");
        if (GL11.glIsEnabled(GL_COLOR_SUM) != saved_GL_COLOR_SUM) errorBuffer.add("GL_COLOR_SUM");
        if (GL11.glIsEnabled(GL_COLOR_TABLE) != saved_GL_COLOR_TABLE) errorBuffer.add("GL_COLOR_TABLE");
        if (GL11.glIsEnabled(GL_CONVOLUTION_1D) != saved_GL_CONVOLUTION_1D) errorBuffer.add("GL_CONVOLUTION_1D");
        if (GL11.glIsEnabled(GL_CONVOLUTION_2D) != saved_GL_CONVOLUTION_2D) errorBuffer.add("GL_CONVOLUTION_2D");
        if (GL11.glIsEnabled(GL_CULL_FACE) != saved_GL_CULL_FACE) errorBuffer.add("GL_CULL_FACE");
        if (GL11.glIsEnabled(GL_DEPTH_TEST) != saved_GL_DEPTH_TEST) errorBuffer.add("GL_DEPTH_TEST");
        if (GL11.glIsEnabled(GL_DITHER) != saved_GL_DITHER) errorBuffer.add("GL_DITHER");
        if (GL11.glIsEnabled(GL_EDGE_FLAG_ARRAY) != saved_GL_EDGE_FLAG_ARRAY) errorBuffer.add("GL_EDGE_FLAG_ARRAY");
        if (GL11.glIsEnabled(GL_FOG) != saved_GL_FOG) errorBuffer.add("GL_FOG");
        if (GL11.glIsEnabled(GL_FOG_COORD_ARRAY) != saved_GL_FOG_COORD_ARRAY) errorBuffer.add("GL_FOG_COORD_ARRAY");
        if (GL11.glIsEnabled(GL_HISTOGRAM) != saved_GL_HISTOGRAM) errorBuffer.add("GL_HISTOGRAM");
        if (GL11.glIsEnabled(GL_INDEX_ARRAY) != saved_GL_INDEX_ARRAY) errorBuffer.add("GL_INDEX_ARRAY");
        if (GL11.glIsEnabled(GL_INDEX_LOGIC_OP) != saved_GL_INDEX_LOGIC_OP) errorBuffer.add("GL_INDEX_LOGIC_OP");
        if (GL11.glIsEnabled(GL_LIGHTING) != saved_GL_LIGHTING) errorBuffer.add("GL_LIGHTING");
        if (GL11.glIsEnabled(GL_LINE_SMOOTH) != saved_GL_LINE_SMOOTH) errorBuffer.add("GL_LINE_SMOOTH");
        if (GL11.glIsEnabled(GL_LINE_STIPPLE) != saved_GL_LINE_STIPPLE) errorBuffer.add("GL_LINE_STIPPLE");
        if (GL11.glIsEnabled(GL_MAP1_COLOR_4) != saved_GL_MAP1_COLOR_4) errorBuffer.add("GL_MAP1_COLOR_4");
        if (GL11.glIsEnabled(GL_MAP1_INDEX) != saved_GL_MAP1_INDEX) errorBuffer.add("GL_MAP1_INDEX");
        if (GL11.glIsEnabled(GL_MAP1_NORMAL) != saved_GL_MAP1_NORMAL) errorBuffer.add("GL_MAP1_NORMAL");
        if (GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_1) != saved_GL_MAP1_TEXTURE_COORD_1) errorBuffer.add("GL_MAP1_TEXTURE_COORD_1");
        if (GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_2) != saved_GL_MAP1_TEXTURE_COORD_2) errorBuffer.add("GL_MAP1_TEXTURE_COORD_2");
        if (GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_3) != saved_GL_MAP1_TEXTURE_COORD_3) errorBuffer.add("GL_MAP1_TEXTURE_COORD_3");
        if (GL11.glIsEnabled(GL_MAP1_TEXTURE_COORD_4) != saved_GL_MAP1_TEXTURE_COORD_4) errorBuffer.add("GL_MAP1_TEXTURE_COORD_4");
        if (GL11.glIsEnabled(GL_MAP2_COLOR_4) != saved_GL_MAP2_COLOR_4) errorBuffer.add("GL_MAP2_COLOR_4");
        if (GL11.glIsEnabled(GL_MAP2_INDEX) != saved_GL_MAP2_INDEX) errorBuffer.add("GL_MAP2_INDEX");
        if (GL11.glIsEnabled(GL_MAP2_NORMAL) != saved_GL_MAP2_NORMAL) errorBuffer.add("GL_MAP2_NORMAL");
        if (GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_1) != saved_GL_MAP2_TEXTURE_COORD_1) errorBuffer.add("GL_MAP2_TEXTURE_COORD_1");
        if (GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_2) != saved_GL_MAP2_TEXTURE_COORD_2) errorBuffer.add("GL_MAP2_TEXTURE_COORD_2");
        if (GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_3) != saved_GL_MAP2_TEXTURE_COORD_3) errorBuffer.add("GL_MAP2_TEXTURE_COORD_3");
        if (GL11.glIsEnabled(GL_MAP2_TEXTURE_COORD_4) != saved_GL_MAP2_TEXTURE_COORD_4) errorBuffer.add("GL_MAP2_TEXTURE_COORD_4");
        if (GL11.glIsEnabled(GL_MAP2_VERTEX_3) != saved_GL_MAP2_VERTEX_3) errorBuffer.add("GL_MAP2_VERTEX_3");
        if (GL11.glIsEnabled(GL_MAP2_VERTEX_4) != saved_GL_MAP2_VERTEX_4) errorBuffer.add("GL_MAP2_VERTEX_4");
        if (GL11.glIsEnabled(GL_MINMAX) != saved_GL_MINMAX) errorBuffer.add("GL_MINMAX");
        if (GL11.glIsEnabled(GL_MULTISAMPLE) != saved_GL_MULTISAMPLE) errorBuffer.add("GL_MULTISAMPLE");
        if (GL11.glIsEnabled(GL_NORMAL_ARRAY) != saved_GL_NORMAL_ARRAY) errorBuffer.add("GL_NORMAL_ARRAY");
        if (GL11.glIsEnabled(GL_NORMALIZE) != saved_GL_NORMALIZE) errorBuffer.add("GL_NORMALIZE");
        if (GL11.glIsEnabled(GL_POINT_SMOOTH) != saved_GL_POINT_SMOOTH) errorBuffer.add("GL_POINT_SMOOTH");
        if (GL11.glIsEnabled(GL_POINT_SPRITE) != saved_GL_POINT_SPRITE) errorBuffer.add("GL_POINT_SPRITE");
        if (GL11.glIsEnabled(GL_POLYGON_SMOOTH) != saved_GL_POLYGON_SMOOTH) errorBuffer.add("GL_POLYGON_SMOOTH");
        if (GL11.glIsEnabled(GL_POLYGON_OFFSET_FILL) != saved_GL_POLYGON_OFFSET_FILL) errorBuffer.add("GL_POLYGON_OFFSET_FILL");
        if (GL11.glIsEnabled(GL_POLYGON_OFFSET_LINE) != saved_GL_POLYGON_OFFSET_LINE) errorBuffer.add("GL_POLYGON_OFFSET_LINE");
        if (GL11.glIsEnabled(GL_POLYGON_OFFSET_POINT) != saved_GL_POLYGON_OFFSET_POINT) errorBuffer.add("GL_POLYGON_OFFSET_POINT");
        if (GL11.glIsEnabled(GL_POLYGON_STIPPLE) != saved_GL_POLYGON_STIPPLE) errorBuffer.add("GL_POLYGON_STIPPLE");
        if (GL11.glIsEnabled(GL_POST_COLOR_MATRIX_COLOR_TABLE) != saved_GL_POST_COLOR_MATRIX_COLOR_TABLE) errorBuffer.add("GL_POST_COLOR_MATRIX_COLOR_TABLE");
        if (GL11.glIsEnabled(GL_POST_CONVOLUTION_COLOR_TABLE) != saved_GL_POST_CONVOLUTION_COLOR_TABLE) errorBuffer.add("GL_POST_CONVOLUTION_COLOR_TABLE");
        if (GL11.glIsEnabled(GL_RESCALE_NORMAL) != saved_GL_RESCALE_NORMAL) errorBuffer.add("GL_RESCALE_NORMAL");
        if (GL11.glIsEnabled(GL_SAMPLE_ALPHA_TO_COVERAGE) != saved_GL_SAMPLE_ALPHA_TO_COVERAGE) errorBuffer.add("GL_SAMPLE_ALPHA_TO_COVERAGE");
        if (GL11.glIsEnabled(GL_SAMPLE_ALPHA_TO_ONE) != saved_GL_SAMPLE_ALPHA_TO_ONE) errorBuffer.add("GL_SAMPLE_ALPHA_TO_ONE");
        if (GL11.glIsEnabled(GL_SAMPLE_COVERAGE) != saved_GL_SAMPLE_COVERAGE) errorBuffer.add("GL_SAMPLE_COVERAGE");
        if (GL11.glIsEnabled(GL_SCISSOR_TEST) != saved_GL_SCISSOR_TEST) errorBuffer.add("GL_SCISSOR_TEST");
        if (GL11.glIsEnabled(GL_SECONDARY_COLOR_ARRAY) != saved_GL_SECONDARY_COLOR_ARRAY) errorBuffer.add("GL_SECONDARY_COLOR_ARRAY");
        if (GL11.glIsEnabled(GL_SEPARABLE_2D) != saved_GL_SEPARABLE_2D) errorBuffer.add("GL_SEPARABLE_2D");
        if (GL11.glIsEnabled(GL_STENCIL_TEST) != saved_GL_STENCIL_TEST) errorBuffer.add("GL_STENCIL_TEST");
        if (GL11.glIsEnabled(GL_TEXTURE_1D) != saved_GL_TEXTURE_1D) errorBuffer.add("GL_TEXTURE_1D");
        if (GL11.glIsEnabled(GL_TEXTURE_2D) != saved_GL_TEXTURE_2D) errorBuffer.add("GL_TEXTURE_2D");
        if (GL11.glIsEnabled(GL_TEXTURE_3D) != saved_GL_TEXTURE_3D) errorBuffer.add("GL_TEXTURE_3D");
        if (GL11.glIsEnabled(GL_TEXTURE_COORD_ARRAY) != saved_GL_TEXTURE_COORD_ARRAY) errorBuffer.add("GL_TEXTURE_COORD_ARRAY");
        if (GL11.glIsEnabled(GL_TEXTURE_CUBE_MAP) != saved_GL_TEXTURE_CUBE_MAP) errorBuffer.add("GL_TEXTURE_CUBE_MAP");
        if (GL11.glIsEnabled(GL_TEXTURE_GEN_Q) != saved_GL_TEXTURE_GEN_Q) errorBuffer.add("GL_TEXTURE_GEN_Q");
        if (GL11.glIsEnabled(GL_TEXTURE_GEN_R) != saved_GL_TEXTURE_GEN_R) errorBuffer.add("GL_TEXTURE_GEN_R");
        if (GL11.glIsEnabled(GL_TEXTURE_GEN_S) != saved_GL_TEXTURE_GEN_S) errorBuffer.add("GL_TEXTURE_GEN_S");
        if (GL11.glIsEnabled(GL_TEXTURE_GEN_T) != saved_GL_TEXTURE_GEN_T) errorBuffer.add("GL_TEXTURE_GEN_T");
        if (GL11.glIsEnabled(GL_VERTEX_ARRAY) != saved_GL_VERTEX_ARRAY) errorBuffer.add("GL_VERTEX_ARRAY");
        if (GL11.glIsEnabled(GL_VERTEX_PROGRAM_POINT_SIZE) != saved_GL_VERTEX_PROGRAM_POINT_SIZE) errorBuffer.add("GL_VERTEX_PROGRAM_POINT_SIZE");
        if (GL11.glIsEnabled(GL_VERTEX_PROGRAM_TWO_SIDE) != saved_GL_VERTEX_PROGRAM_TWO_SIDE) errorBuffer.add("GL_VERTEX_PROGRAM_TWO_SIDE");

        if (saved_GL_ALPHA_TEST) {
            // these are only significant if original state have alpha test on
            if (GL11.glGetInteger(GL_ALPHA_TEST_FUNC) != saved_GL_ALPHA_TEST_FUNC) errorBuffer.add("GL_ALPHA_TEST_FUNC");
            if (GL11.glGetDouble(GL_ALPHA_TEST_REF) != saved_GL_ALPHA_TEST_REF) errorBuffer.add("GL_ALPHA_TEST_REF");
        }

        if (saved_GL_DEPTH_TEST) {
            // these are only significant if original state have depth test on
            if (GL11.glGetInteger(GL_DEPTH_FUNC) != saved_GL_DEPTH_FUNC) errorBuffer.add("GL_DEPTH_FUNC");
        }

        if (saved_GL_BLEND) {
            // these are only significant if original state have blending on
            if (GL11.glGetInteger(GL_BLEND_DST_ALPHA) != saved_GL_BLEND_DST_ALPHA) errorBuffer.add("GL_BLEND_DST_ALPHA");
            if (GL11.glGetInteger(GL_BLEND_DST_RGB) != saved_GL_BLEND_DST_RGB) errorBuffer.add("GL_BLEND_DST_RGB");
            if (GL11.glGetInteger(GL_BLEND_SRC_ALPHA) != saved_GL_BLEND_SRC_ALPHA) errorBuffer.add("GL_BLEND_SRC_ALPHA");
            if (GL11.glGetInteger(GL_BLEND_SRC_RGB) != saved_GL_BLEND_SRC_RGB) errorBuffer.add("GL_BLEND_SRC_RGB");
        }

        return errorBuffer.isEmpty();
    }

    public static String compose() {
        return String.join(", ", errorBuffer);
    }
}
// @formatter:on
// spotless:on
