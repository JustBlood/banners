CREATE OR REPLACE FUNCTION banner_audit_function()
    RETURNS TRIGGER AS
'
BEGIN
    INSERT INTO banner_audit (banner_id, feature_id, content, tags, is_active, operation_type)
    SELECT OLD.banner_id, OLD.feature_id, OLD.content, array_agg(tag_id), OLD.is_active, TG_OP
    FROM banner_feature_tag AS bft WHERE bft.banner_id = OLD.banner_id;
    IF TG_OP = ''DELETE'' THEN
        RETURN OLD;
    END IF;
    RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER on_banner_update_delete_trigger
    BEFORE DELETE OR UPDATE ON banner
    FOR EACH ROW
EXECUTE PROCEDURE banner_audit_function();
