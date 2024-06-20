DELETE FROM public.banner;
DELETE FROM public."user" WHERE token ILIKE '%token%';
