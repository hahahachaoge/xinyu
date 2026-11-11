-- Incremental repair for an existing, empty demo record. Back up the database first.
-- Does not overwrite a document that already has an attachment or chunks.
UPDATE knowledge_document
SET file_url = '/api/1000000040/心理健康自我保健知识.md',
    file_format = 'MD', file_size = '1.9KB',
    doc_desc = '项目根据世界卫生组织及中科院心理研究所公开科普资料整理的学习稿，2026-11-11补充；非机构原文，未经临床专家审定。各节标注来源，完整链接见文末。',
    vector_status_type = 1, vector_time = NULL
WHERE id = 4 AND file_url IS NULL
  AND NOT EXISTS (SELECT 1 FROM document_chunk WHERE knowledge_document_id = 4);
-- Then sign in as administrator and select document 4 -> Re-vectorize.
