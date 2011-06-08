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

package net.wigis.graph.data.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.settings.Settings;
import au.com.bytecode.opencsv.CSVReader;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateFilesFromCSV.
 * 
 * @author Brynjar Gretarsson
 */
public class CreateFilesFromCSV
{

	/**
	 * Creates the.
	 * 
	 * @param rootDirectory
	 *            the root directory
	 * @param files
	 *            the files
	 * @param headers
	 *            the headers
	 * @param contentHeader
	 *            the content header
	 * @param outputFileNameHeaders
	 *            the output file name headers
	 * @param docsFile
	 *            the docs file
	 * @param metaDocsFile
	 *            the meta docs file
	 * @param outputDirectory
	 *            the output directory
	 * @param filterHeader
	 *            the filter header
	 * @param filterValues
	 *            the filter values
	 * @param departmentsFile
	 *            the departments file
	 * @param departmentFields
	 *            the department fields
	 */
	public static void create( String rootDirectory, String files[], String headers[], String contentHeader, String outputFileNameHeaders[],
			String docsFile, String metaDocsFile, String outputDirectory, String filterHeader, String filterValues[], String departmentsFile,
			String departmentFields[] )
	{
		StringBuilder outputFiles = new StringBuilder();
		StringBuilder metaFiles = new StringBuilder();
		HashMap<String, String> departments = new HashMap<String, String>();
		HashMap<String, String> fields = new HashMap<String, String>();
		new File( rootDirectory + outputDirectory ).mkdirs();
		List<String> colorList = DetermineAuthorExpertise.getColorList();
		for( int i = 0; i < files.length; i++ )
		{
			try
			{
				CSVReader csv = new CSVReader( new FileReader( new File( rootDirectory, files[i] ) ) );
				String line[] = csv.readNext();
				int[] indices = new int[headers.length];
				for( int k = 0; k < headers.length; k++ )
				{
					indices[k] = -1;
				}
				int[] outputFileNameIndices = new int[outputFileNameHeaders.length];
				for( int k = 0; k < outputFileNameHeaders.length; k++ )
				{
					outputFileNameIndices[k] = -1;
				}
				int[] departmentFieldsIndices = new int[departmentFields.length];
				for( int k = 0; k < departmentFieldsIndices.length; k++ )
				{
					departmentFieldsIndices[k] = -1;
				}
				int filterIndex = -1;

				for( int j = 0; j < line.length; j++ )
				{
					for( int k = 0; k < headers.length; k++ )
					{
						if( line[j].equals( headers[k] ) )
						{
							indices[k] = j;
							break;
						}
					}

					for( int k = 0; k < outputFileNameHeaders.length; k++ )
					{
						if( line[j].equals( outputFileNameHeaders[k] ) )
						{
							outputFileNameIndices[k] = j;
							break;
						}
					}

					for( int k = 0; k < departmentFields.length; k++ )
					{
						if( line[j].equals( departmentFields[k] ) )
						{
							departmentFieldsIndices[k] = j;
						}
					}

					if( line[j].equals( filterHeader ) )
					{
						filterIndex = j;
					}
				}

				String metaOutput;
				String contentOutput;
				String fileName;
				String metaFileName;
				int j = 1;
				String checker;
				while( ( line = csv.readNext() ) != null )
				{
					checker = line[filterIndex];
					if( include( checker, filterValues ) )
					{
						metaOutput = "";
						contentOutput = "";
						for( int k = 0; k < indices.length; k++ )
						{
							metaOutput += "<" + headers[k] + ">" + line[indices[k]] + "</" + headers[k] + ">" + "\n";
							if( headers[k].equals( contentHeader ) )
							{
								contentOutput = line[indices[k]];
							}
						}

						fileName = "";
						for( int k = 0; k < outputFileNameIndices.length; k++ )
						{
							fileName += line[outputFileNameIndices[k]] + " ";
						}
						fileName = fileName.trim();
						fileName = fileName.replaceAll( "/", "-" );
						fileName = fileName.replaceAll( ":", "-" );
						fileName = fileName.replaceAll( "\"", "-" );
						fileName = fileName.replaceAll( "\\*", "-star" );
						fileName = fileName.replaceAll( "\\?", "." );
						fileName = outputDirectory + fileName;
						metaFileName = fileName + "_meta.txt";
						fileName += ".txt";
						outputFiles.append( fileName ).append( "\n" );
						metaFiles.append( metaFileName ).append( "\n" );

						String value = departments.get( line[departmentFieldsIndices[0]] );
						if( value == null )
						{
							value = "";
							for( int k = 0; k < departmentFieldsIndices.length; k++ )
							{
								value += "\"" + line[departmentFieldsIndices[k]] + "\",";
							}
							departments.put( line[departmentFieldsIndices[0]], value );
						}

						value = fields.get( line[departmentFieldsIndices[2]] );
						if( value == null )
						{
							String color;
							try
							{
								color = colorList.remove( 0 );
							}
							catch( IndexOutOfBoundsException e )
							{
								colorList = DetermineAuthorExpertise.getColorList();
								color = colorList.remove( 0 );
							}
							value = "\"" + line[departmentFieldsIndices[2]] + "\",\"" + line[departmentFieldsIndices[2]] + "\"," + color;
							System.out.println( line[departmentFieldsIndices[2]] + "->" + value );
							fields.put( line[departmentFieldsIndices[2]], value );
						}

						writeFile( rootDirectory + fileName, contentOutput );
						writeFile( rootDirectory + metaFileName, metaOutput );
						// System.out.println( j );
						j++;
					}
				}
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		StringBuilder departmentsOutput = new StringBuilder();
		for( int k = 0; k < departmentFields.length; k++ )
		{
			departmentsOutput.append( departmentFields[k] ).append( "," );
		}
		departmentsOutput.append( "\n" );
		for( String key : departments.keySet() )
		{
			departmentsOutput.append( departments.get( key ) ).append( "\n" );
		}

		System.out.println( departments.keySet().size() + " different values." );

		StringBuilder fieldsOutput = new StringBuilder();
		fieldsOutput.append( "All Departments:,Field Mapping:,Color Mapping\n" );
		for( String key : fields.keySet() )
		{
			fieldsOutput.append( fields.get( key ) ).append( "\n" );
		}

		writeFile( rootDirectory + "departmentToColorMapping.txt", fieldsOutput.toString() );
		writeFile( rootDirectory + departmentsFile, departmentsOutput.toString() );
		writeFile( rootDirectory + docsFile, outputFiles.toString() );
		writeFile( rootDirectory + metaDocsFile, metaFiles.toString() );
	}

	/**
	 * Include.
	 * 
	 * @param checker
	 *            the checker
	 * @param filterValues
	 *            the filter values
	 * @return true, if successful
	 */
	private static boolean include( String checker, String[] filterValues )
	{
		for( int i = 0; i < filterValues.length; i++ )
		{
			if( filterValues[i].equals( checker ) )
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Write file.
	 * 
	 * @param file
	 *            the file
	 * @param output
	 *            the output
	 */
	private static void writeFile( String file, String output )
	{
		try
		{
			FileWriter fw = new FileWriter( new File( file ) );
			BufferedWriter bw = new BufferedWriter( fw );
			bw.write( output );
			bw.close();
			fw.close();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		String files[] = { "NSF-awards-Oct-2005_to_June-2007-Feb03.csv", "NSF-awards-July-2007_to_Sept-2008-Feb03.csv",
				"NSF-awards-Oct-2008_to_Jan-2010-Feb03.csv" };

		String headers[] = { "AWARDEE", "DOING_BUSINESS_AS_NAME", "PI_NAME", "COPI", "ESTIMATED_TOTAL_AWARD_AMOUNT", "AWARD_START_DATE",
				"AWARD_EXPIRATION_DATE", "TRANSACTION_TYPE", "AGENCY", "CFDA_NUMBER", "PRIMARY_PROGRAM_SOURCE", "AWARD_TITLE_OR_DESCRIPTION",
				"FEDERAL_AWARD_ID_NUMBER", "DUNS_ID", "PARENT_DUNS_ID", "PROGRAM_NAME", "PROGRAM_OFFICER_EMAIL", "PROGRAM_OFFICER_NAME",
				"AWARDEE_CITY", "AWARDEE_STATE", "AWARDEE_COUNTY", "AWARDEE_COUNTRY", "AWARDEE_CONG_DISTRICT", "PERFORMING_ORG_NAME",
				"PERFORMING_CITY", "PERFORMING_STATE", "PERFORMING_COUNTY", "PERFORMING_COUNTRY", "PERFORMING_CONG_DISTRICT",
				"ABSTRACT_AT_TIME_OF_AWARD", };

		String contentHeader = "ABSTRACT_AT_TIME_OF_AWARD";

		String outputFileNameHeaders[] = {
		// "FEDERAL_AWARD_ID_NUMBER",
		"AWARD_TITLE_OR_DESCRIPTION" };

		String filterHeader = "PROGRAM_NAME";
		String filterValues[] = { "COMPUTER SYSTEMS", "ROBUST INTELLIGENCE", "INFO INTEGRATION & INFORMATICS", "HUMAN-CENTERED COMPUTING",
				"COMPUTATIONAL MATHEMATICS", "CYBER TRUST", "INTEGRATIVE, HYBRD & COMPLX SY", "INFORMAL SCIENCE EDUCATION",
				// "BROADENING PARTIC IN COMPUTING",
				"TRUSTWORTHY COMPUTING", "NUMERIC, SYMBOLIC & GEO COMPUT",
				// "SCIENCE OF SCIENCE POLICY",
				"CDI TYPE II", "ALGORITHMIC FOUNDATIONS", "CYBER-PHYSICAL SYSTEMS (CPS)",
				// "ITR-CreativeIT",
				// "IIS SPECIAL PROJECTS",
				"NETWORK SCIENCE & ENGINEERING", "GRAPHICS & VISUALIZATION",
		// "CDI TYPE I",
		// "SOFTWARE FOR REAL-WORLD SYSTMS",
		// "DATA-INTENSIVE COMPUTING",
		// "ITR-CYBERTRUST",
		// "CISE EDUCAT RES & CURRIC DEVEL"
		};
		String docsFile = "docs.txt";
		String metaDocsFile = "metaDocs.txt";
		String outputDirectory = "data/";
		GraphsPathFilter gpf = new GraphsPathFilter();
		try
		{
			gpf.init( null );
		}
		catch( ServletException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String rootDirectory = Settings.GRAPHS_PATH + "topicVisualizations/NSF/";
		String departmentsFile = "department.txt";
		String departmentFields[] = { "PROGRAM_NAME", "DOING_BUSINESS_AS_NAME", "PROGRAM_NAME", "PROGRAM_OFFICER_NAME", "PI_NAME" };

		create( rootDirectory, files, headers, contentHeader, outputFileNameHeaders, docsFile, metaDocsFile, outputDirectory, filterHeader,
				filterValues, departmentsFile, departmentFields );
	}
}
