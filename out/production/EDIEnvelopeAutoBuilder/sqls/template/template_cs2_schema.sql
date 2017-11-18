SET SCAN OFF

-- MD5: @MD5
-- schema zip Script: @NAME
-- Reference from Script: @REFNAME


SELECT name, version, category_desc, description, create_ts, created_by FROM b2b_edi_classes_ref WHERE NAME = '@NAME' ORDER BY to_number(VERSION) DESC;

DECLARE 

ScriptVersion number;
NewScriptVersion number;

@VAR64

BEGIN
    
    NewScriptVersion := to_char(sysdate,'yyMMddHH24');

    INSERT INTO b2b_edi_classes_ref(NAME, VERSION, CATEGORY_DESC, DESCRIPTION, CREATE_TS, CREATED_BY, CLASS_FILE_BASE64)
    VALUES('@NAME', NewScriptVersion, 'schema', 'CS2 Schema' , SYSDATE, 'AUTO_GENERATE', @VARIABLE_64);

END;
/

COMMIT;

SELECT name, version, category_desc, description, create_ts, created_by FROM b2b_edi_classes_ref WHERE NAME = '@NAME' ORDER BY to_number(VERSION) DESC;

