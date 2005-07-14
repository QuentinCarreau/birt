/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.core.commands;

import org.eclipse.birt.report.designer.core.DesignerConstants;
import org.eclipse.birt.report.designer.core.model.schematic.HandleAdapterFactory;
import org.eclipse.birt.report.designer.core.model.schematic.TableHandleAdapter;
import org.eclipse.birt.report.designer.util.DEUtil;
import org.eclipse.birt.report.model.api.ColumnHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.util.Assert;

/**
 * This command deletes an object from the ColumnHandle.
 * 
 */
public class DeleteColumnCommand extends Command
{

	private ColumnHandle handle = null;

	/**
	 * Deletes the command
	 * 
	 * @param model
	 *            the model
	 */

	public DeleteColumnCommand( Object model )
	{
		Assert.isTrue( model instanceof ColumnHandle );
		this.handle = (ColumnHandle) model;
	}

	/**
	 * Executes the Command. This method should not be called if the Command is
	 * not executable.
	 */

	public void execute( )
	{
		if ( getTableParent( ) != null )
		{
			if ( DesignerConstants.TRACING_COMMANDS )
			{
				System.out.println( "DeleteColumnCommand >> Starts. Target: " //$NON-NLS-1$
						+ DEUtil.getDisplayLabel( getTableParent( ) ) );
			}
			TableHandleAdapter tableHandle = HandleAdapterFactory.getInstance( )
					.getTableHandleAdapter( getTableParent( ) );

			int columnNumber = HandleAdapterFactory.getInstance( )
					.getColumnHandleAdapter( handle )
					.getColumnNumber( );

			try
			{
				tableHandle.deleteColumn( new int[]{
					columnNumber
				} );
				if ( DesignerConstants.TRACING_COMMANDS )
				{
					System.out.println( "DeleteColumnCommand >> Finished. " );					 //$NON-NLS-1$
				}
			}
			catch ( SemanticException e )
			{
				if ( DesignerConstants.TRACING_COMMANDS )
				{
					System.out.println( "DeleteColumnCommand >> Failed. " ); //$NON-NLS-1$
				}
			}
		}
	}

	private Object getTableParent( )
	{
		DesignElementHandle parent = handle.getContainer( );
		while ( parent != null )
		{
			if ( parent instanceof TableHandle || parent instanceof GridHandle )
			{
				return parent;
			}
			parent = parent.getContainer( );
		}
		return null;
	}
}