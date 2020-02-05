package fr.fredos.dvdtheque.common.enums;

public enum JmsStatus {
	CLEAN_DB_INIT(0),
	CLEAN_DB_COMPLETED(1),
	IMPORT_INIT(0), 
	IMPORT_COMPLETED_SUCCESS(1),
	IMPORT_COMPLETED_ERROR(2),
	FILM_PROCESSOR_INIT(0), 
	FILM_PROCESSOR_COMPLETED(1),
	FILE_ITEM_READER_INIT(0),
	FILE_ITEM_READER_COMPLETED(1),
	FILM_CSV_LINE_MAPPER_INIT(0),
	FILM_CSV_LINE_MAPPER_COMPLETED(1),
	FILM_CSV_LINE_TOKENIZER_INIT(0),
	FILM_CSV_LINE_TOKENIZER_COMPLETED(1),
	DB_FILM_WRITER_INIT(0),
	DB_FILM_WRITER_COMPLETED(1);
	
	private int statusValue;
	JmsStatus(int statusValue){
		this.statusValue = statusValue;
	}
	public int statusValue() {
        return statusValue;
    }
}
