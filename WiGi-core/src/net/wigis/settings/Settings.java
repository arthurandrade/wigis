/******************************************************************************************************
 * Copyright (c) 2010, University of California, Santa Barbara
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 *    * Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials 
 *      provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *****************************************************************************************************/

package net.wigis.settings;

import net.wigis.graph.dnv.interaction.implementations.InterpolationMethod;
import net.wigis.graph.dnv.interaction.implementations.InterpolationWithSpring;
import net.wigis.graph.dnv.interaction.implementations.SpringInteraction;
import net.wigis.graph.dnv.interaction.interfaces.InteractionInterface;
import net.wigis.graph.dnv.layout.implementations.*;
import net.wigis.graph.dnv.layout.implementations.DisjointGraphLayout;
import net.wigis.graph.dnv.layout.implementations.FruchtermanReingold;
import net.wigis.graph.dnv.layout.implementations.MDSLayout;
import net.wigis.graph.dnv.layout.implementations.Springs;
import net.wigis.graph.dnv.layout.implementations.TouchGraphLayout;
import net.wigis.graph.dnv.layout.interfaces.LayoutInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class Settings.
 * 
 * Defines constant settings and default values for the WiGi system.
 * 
 * @author Brynjar Gretarsson
 */
public class Settings
{
	// Defines the default size of the detail window
	/** The Constant WIDTH. */
	public static final int WIDTH = 600;

	/** The Constant HEIGHT. */
	public static final int HEIGHT = 600;

	/** The Constant MAC_GRAPHS_PATH. */
	public static final String MAC_GRAPHS_PATH = "/graphs/";

	/** The Constant PC_GRAPHS_PATH. */
	public static final String PC_GRAPHS_PATH = "C:\\graphs\\";

	/** The GRAPH s_ path. */
	public static String GRAPHS_PATH = PC_GRAPHS_PATH;

	/** The Constant DEFAULT_GRAPH. */
	public static final String DEFAULT_GRAPH = "_UCI_venezuela.dnv";

	/** The Constant UPLOADS_DIRECTORY. */
	public static final String UPLOADS_DIRECTORY = GRAPHS_PATH + "csvfiles\\";

	// public static final String IMAGES_FILEPATH = "/images/nodeicons/";

	// public static final String WEBPATH = "/blackbook/wigi/";
	/** The Constant WEBPATH. */
//	public static final String WEBPATH = "/WiGi/wigi/";

	/** The Constant DEFAULT_JS_THRESHOLD. */
	public static final int DEFAULT_JS_THRESHOLD = 0;

	/** The Constant DEFAULT_SHOW_ICONS. */
	public static final boolean DEFAULT_SHOW_ICONS = true;

	// =================================================
	// normal settings
	/** The Constant DEFAULT_SHOW_LABELS. */
	public static final boolean DEFAULT_SHOW_LABELS = true;
	/** The Constant DEFAULT_HIDE_CONFLICTING_LABELS. */
	public static final boolean DEFAULT_HIDE_CONFLICTING_LABELS = true;
	/** The Constant DEFAULT_MAX_NUMBER_OF_SELECTED_LABELS. */
	public static final int DEFAULT_MAX_NUMBER_OF_SELECTED_LABELS = 10;
	/** The Constant DEFAULT_LABEL_SIZE. */
	public static final int DEFAULT_LABEL_SIZE = 12;
	public static final boolean DEFAULT_SHOW_RELOAD_BUTTON = false;
	public static final boolean DEFAULT_HIGHLIGHT_NEIGHBORS = true;
	public static final boolean DEFAULT_SELECTED_NODE_DETAILS_EXPANDED = false;
	public static boolean CURVED_EDGES = false;
	// =================================================

	// =================================================
	// special settings for Enrico Glaab
//	/** The Constant DEFAULT_SHOW_LABELS. */
//	public static final boolean DEFAULT_SHOW_LABELS = false;
//	/** The Constant DEFAULT_HIDE_CONFLICTING_LABELS. */
//	public static final boolean DEFAULT_HIDE_CONFLICTING_LABELS = false;
//	/** The Constant DEFAULT_MAX_NUMBER_OF_SELECTED_LABELS. */
//	public static final int DEFAULT_MAX_NUMBER_OF_SELECTED_LABELS = 20;
//	/** The Constant DEFAULT_LABEL_SIZE. */
//	public static final int DEFAULT_LABEL_SIZE = 10;
//	public static final boolean DEFAULT_SHOW_RELOAD_BUTTON = true;
//	public static final boolean DEFAULT_HIGHLIGHT_NEIGHBORS = true;
//	public static final boolean DEFAULT_SELECTED_NODE_DETAILS_EXPANDED = true;
	// =================================================

	// public static boolean BLACKBOOK = false;

	/** The INTERPOLATIO n_ labeling. */
	public static boolean INTERPOLATION_LABELING = false;

	// Used for benchmark testing
	/** The Constant INTERPOLATION_TEST. */
	public static final boolean INTERPOLATION_TEST = false;

	/** The Constant IMAGE_MOVING_TEST. */
	public static final boolean IMAGE_MOVING_TEST = false;

	/** The Constant CLIENT_SIDE_TEST. */
	public static final boolean CLIENT_SIDE_TEST = false;

	/** The Constant SERVER_SIDE_TEST. */
	public static final boolean SERVER_SIDE_TEST = false;

	/** The Constant DISABLE_IMAGES. */
	public static final boolean DISABLE_IMAGES = false;

	// A flag that indicates if debug fields should be displayed on the page.
	/** The SHO w_ debu g_ fields. */
	public static boolean SHOW_DEBUG_FIELDS = false;

	// A flag that indicates if debug information should be output in server
	// log.
	/** The DEBUG. */
	public static boolean DEBUG = false;

	// Layout algorithms

//	/** The Constant CIRCULAR_LAYOUT. */
//	public static final String CIRCULAR_LAYOUT = "Circular Layout";
//
//	/** The Constant RECOMMENDATION_LAYOUT. */
//	public static final String RECOMMENDATION_LAYOUT = "Recommendation Layout";
//
//	/** The Constant PEERCHOOSER_LAYOUT. */
//	public static final String PEERCHOOSER_LAYOUT = "Peerchooser Layout";
//
//	/** The Constant DISJOINT_GRAPH_LAYOUT. */
//	public static final String DISJOINT_GRAPH_LAYOUT = "Disjoint Graph Layout";
//
//	/** The Constant MDS_LAYOUT. */
//	public static final String MDS_LAYOUT = "MDS Layout";
//
//	/** The Constant DOCUMENT_TOPIC_SPIRAL_LAYOUT. */
//	public static final String DOCUMENT_TOPIC_SPIRAL_LAYOUT = "Document Topic Spiral Layout";
//
	/** The Constant DOCUMENT_TOPIC_CIRCULAR_LAYOUT. */
	public static final String DOCUMENT_TOPIC_CIRCULAR_LAYOUT = "Document Topic Circular Layout";
//
//	/** The Constant DOCUMENT_TOPIC_RECTANGULAR_LAYOUT. */
//	public static final String DOCUMENT_TOPIC_RECTANGULAR_LAYOUT = "Document Topic Rectangular Layout";
//
//	/** The Constant DOCUMENT_TOPIC_MDS_LAYOUT. */
//	public static final String DOCUMENT_TOPIC_MDS_LAYOUT = "Document Topic MDS Layout";

	/** The LAYOU t_ algorithms. */
	public static LayoutInterface[] LAYOUT_ALGORITHMS = { new FM3Layout(), new BinaryStressLayout(), new FruchtermanReingold(), new CircularLayout(), new MDSLayout(), new DisjointGraphLayout(), new TouchGraphLayout(), new Springs() };

	// Clustering algorithms
	/** The Constant K_MOST_CONNECTED_CLUSTERING. */
	public static final String K_MOST_CONNECTED_CLUSTERING = "K Most Connected Clustering";

	/** The Constant SOLAR_SYSTEM_CLUSTERING. */
	public static final String SOLAR_SYSTEM_CLUSTERING = "Solar System Clustering";

	/** The Constant CONNECTED_CLUSTERING. */
	public static final String CONNECTED_CLUSTERING = "Connected Clustering";

	/** The Constant STRUCTURAL_EQUIVALENCE_CLUSTERING. */
	public static final String STRUCTURAL_EQUIVALENCE_CLUSTERING = "Structural Equivalence Clustering";

	/** The CLUSTERIN g_ algorithms. */
	public static String[] CLUSTERING_ALGORITHMS = { K_MOST_CONNECTED_CLUSTERING, STRUCTURAL_EQUIVALENCE_CLUSTERING };

	// Interaction algorithms
//	/** The Constant INTERPOLATION_INTERACTION. */
//	public static final String INTERPOLATION_INTERACTION = "Interpolation Method";
//	
//	public static final String SIMPLE_INTERACTION = "Simple Interaction";
//
//	/** The Constant PEERCHOOSER_INTERACTION. */
//	public static final String PEERCHOOSER_INTERACTION = "Peerchooser Interaction";
//
//	/** The Constant FACEBOOK_RECOMMENDATION_INTERACTION. */
//	public static final String FACEBOOK_RECOMMENDATION_INTERACTION = "Facebook Recommendation Interaction";
//
//	/** The Constant TOPIC_INTERACTION. */
//	public static final String TOPIC_INTERACTION = "Document Topic Interaction";
//	
//	public static final String SPRING_INTERACTION = "Spring Interaction";
//
//	public static final String INTERPOLATION_WITH_SPRING = "Interpolation with Spring Interaction";
	
	/** The INTERACTIO n_ algorithms. */
	public static InteractionInterface[] INTERACTION_ALGORITHMS = { new InterpolationMethod(), new SpringInteraction(), new InterpolationWithSpring() };
}
