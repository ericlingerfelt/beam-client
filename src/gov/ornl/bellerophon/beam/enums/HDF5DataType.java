package gov.ornl.bellerophon.beam.enums;

import java.util.ArrayList;

import hdf.object.Attribute;
import hdf.object.Datatype;
import hdf.object.h5.H5ScalarDS;
import hdf.object.h5.H5CompoundDS;

public enum HDF5DataType {

	CHAR,
	BYTE,
	SHORT,
	INT, 
	LONG, 
	FLOAT,
	DOUBLE;
	
	public static HDF5DataType getHDF5DataType(H5ScalarDS ds){
		switch(ds.getDatatype().getDatatypeClass()){
			case Datatype.CLASS_CHAR:
				switch(ds.getDatatype().getDatatypeSize()){
					case 1:
						return CHAR;
				}
			case Datatype.CLASS_INTEGER:
				switch(ds.getDatatype().getDatatypeSize()){
					case 1:
						return BYTE;
					case 2: 
						return SHORT;
					case 4: 
						return INT;
					case 8:
						return LONG;
				}
			case Datatype.CLASS_FLOAT:
				switch(ds.getDatatype().getDatatypeSize()){
					case 4: 
						return FLOAT;
					case 8:
						return DOUBLE;
			}
		}
		return null;
	}
	
	public static ArrayList<HDF5DataType> getHDF5DataType(H5CompoundDS ds){
		
		ArrayList<HDF5DataType> dsDataType = new ArrayList<HDF5DataType>();
		
		Datatype[] dsMemberType = ds.getMemberTypes();
		
		for(int i=0; i<dsMemberType.length; i++) {
			switch(dsMemberType[i].getDatatypeClass()){
				case Datatype.CLASS_CHAR:
					switch(dsMemberType[i].getDatatypeSize()){
						case 1:
							dsDataType.add(CHAR);
					}
				case Datatype.CLASS_INTEGER:
					switch(dsMemberType[i].getDatatypeSize()){
						case 1:
							dsDataType.add(BYTE);
						case 2: 
							dsDataType.add(SHORT);
						case 4: 
							dsDataType.add(INT);
						case 8:
							dsDataType.add(LONG);
					}
				case Datatype.CLASS_FLOAT:
					switch(dsMemberType[i].getDatatypeSize()){
						case 4: 
							dsDataType.add(FLOAT);
						case 8:
							dsDataType.add(DOUBLE);
					}
			}
		}
		
		return ( dsDataType.size() > 0 ? dsDataType : null );
	}
	
	public static HDF5DataType getHDF5DataType(Attribute a){
		switch(a.getType().getDatatypeClass()){
			case Datatype.CLASS_CHAR:
				switch(a.getType().getDatatypeSize()){
					case 1:
						return CHAR;
				}
			case Datatype.CLASS_INTEGER:
				switch(a.getType().getDatatypeSize()){
					case 1:
						return BYTE;
					case 2: 
						return SHORT;
					case 4: 
						return INT;
					case 8:
						return LONG;
				}
			case Datatype.CLASS_FLOAT:
				switch(a.getType().getDatatypeSize()){
					case 4: 
						return FLOAT;
					case 8:
						return DOUBLE;
			}
		}
		return null;
	}
	
}
