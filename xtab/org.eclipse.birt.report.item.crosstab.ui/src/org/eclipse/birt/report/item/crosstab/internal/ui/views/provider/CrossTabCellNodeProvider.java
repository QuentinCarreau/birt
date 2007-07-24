/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.item.crosstab.internal.ui.views.provider;

import java.util.Map;

import org.eclipse.birt.report.designer.internal.ui.extension.experimental.EditpartExtensionManager;
import org.eclipse.birt.report.designer.internal.ui.extension.experimental.PaletteEntryExtension;
import org.eclipse.birt.report.designer.internal.ui.processor.ElementProcessorFactory;
import org.eclipse.birt.report.designer.internal.ui.views.DefaultNodeProvider;
import org.eclipse.birt.report.designer.internal.ui.views.IRequestConstants;
import org.eclipse.birt.report.designer.internal.ui.views.actions.InsertAction;
import org.eclipse.birt.report.designer.ui.ReportPlatformUIImages;
import org.eclipse.birt.report.designer.util.DNDUtil;
import org.eclipse.birt.report.item.crosstab.core.de.CrosstabCellHandle;
import org.eclipse.birt.report.item.crosstab.core.de.CrosstabViewHandle;
import org.eclipse.birt.report.item.crosstab.core.de.LevelViewHandle;
import org.eclipse.birt.report.item.crosstab.core.de.MeasureViewHandle;
import org.eclipse.birt.report.item.crosstab.internal.ui.util.CrosstabUIHelper;
import org.eclipse.birt.report.item.crosstab.ui.i18n.Messages;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

public class CrossTabCellNodeProvider extends DefaultNodeProvider
{

	public Object[] getChildren( Object model )
	{
		ExtendedItemHandle element = (ExtendedItemHandle) model;
		try
		{
			CrosstabCellHandle cell = (CrosstabCellHandle) element.getReportItem( );
			if ( cell != null )
				return cell.getContents( ).toArray( );
		}
		catch ( ExtendedElementException e )
		{
		}
		return new Object[0];
	}

	public Object getParent( Object model )
	{
		ExtendedItemHandle element = (ExtendedItemHandle) model;
		try
		{
			CrosstabCellHandle cell = (CrosstabCellHandle) element.getReportItem( );
			if ( cell.getContainer( ) != null )
			{
				if ( cell.getContainer( ) instanceof MeasureViewHandle )
				{
					MeasureViewHandle measure = (MeasureViewHandle) cell.getContainer( );
					PropertyHandle property = cell.getModelHandle( )
							.getContainerPropertyHandle( );
					return measure.getModelHandle( )
							.getPropertyHandle( property.getPropertyDefn( )
									.getName( ) );
				}
				else if ( cell.getContainer( ) instanceof LevelViewHandle
						|| cell.getContainer( ) instanceof CrosstabViewHandle )
				{
					return cell.getContainer( ).getModelHandle( );
				}

			}
		}
		catch ( ExtendedElementException e )
		{
		}
		return null;
	}

	public boolean hasChildren( Object model )
	{
		return getChildren( model ).length != 0;
	}

	public String getNodeDisplayName( Object model )
	{
		return Messages.getString( "CrossTabCellNodeProvider.Cell" ); //$NON-NLS-1$
	}

	public Image getNodeIcon( Object element )
	{
		if ( element instanceof DesignElementHandle
				&& ( (DesignElementHandle) element ).getSemanticErrors( )
						.size( ) > 0 )
		{
			return ReportPlatformUIImages.getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
		}
		return CrosstabUIHelper.getImage( CrosstabUIHelper.CELL_IMAGE );
	}

	public boolean performRequest( Object model, Request request )
			throws Exception
	{
		if ( request.getType( ).equals( IRequestConstants.REQUEST_TYPE_INSERT ) )
		{
			Map extendsData = request.getExtendedData( );
			PropertyHandle propertyHandle = (PropertyHandle) extendsData.get( IRequestConstants.REQUEST_KEY_INSERT_PROPERTY );
			String type = (String) extendsData.get( IRequestConstants.REQUEST_KEY_INSERT_TYPE );
			String position = (String) extendsData.get( IRequestConstants.REQUEST_KEY_INSERT_POSITION );
			return performInsert( model,
					propertyHandle,
					type,
					position,
					extendsData );
		}
		
		return super.performRequest(model, request );
	}
	
	protected boolean performInsert( Object model, PropertyHandle propertyHandle,
			String type, String position, Map extendData ) throws Exception
	{		
		PaletteEntryExtension[] entries = EditpartExtensionManager.getPaletteEntries( );
		for ( int i = 0; i < entries.length; i++ )
		{
			if ( entries[i].getItemName( ).equals( type ) )
			{
				extendData.put( IRequestConstants.REQUEST_KEY_RESULT, entries[i].executeCreate( ) );
				return true;
			}
		}
		
		DesignElementHandle elementHandle = createElement( type );

		if ( extendData != null )
		{
			extendData.put( IRequestConstants.REQUEST_KEY_RESULT, elementHandle );
		}

		if ( elementHandle == null )
		{
			return false;
		}
//		if ( position == InsertAction.CURRENT )
//		{
//			slotHandle.add( elementHandle );
//		}
		else
		{
			int pos = DNDUtil.calculateNextPosition( model,
					DNDUtil.handleValidateTargetCanContain( model,
							elementHandle,
							true ) );
			if ( pos > 0 && position == InsertAction.ABOVE )
			{
				pos--;
			}

			if ( pos == -1 )
			{
				propertyHandle.add( elementHandle );
			}
			else
			{
				propertyHandle.add( elementHandle, pos );
			}
		}

		// fix bugzilla#145284
		// TODO check extension setting here to decide if popup the builder
		if ( elementHandle instanceof ExtendedItemHandle )
		{
			if ( ElementProcessorFactory.createProcessor( elementHandle ) != null
					&& !ElementProcessorFactory.createProcessor( elementHandle )
							.editElement( elementHandle ) )
			{
				return false;
			}
		}
		return true;
	}
}
