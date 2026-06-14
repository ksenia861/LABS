SELECT * FROM city;
SELECT * FROM people;
SELECT * FROM goal;
SELECT * FROM feeling;
SELECT * FROM people_feeling;
SELECT * FROM people_city;
SELECT 
    p.name AS человек,
    c.name AS город,
    f.name AS чувство
FROM 
    people p
LEFT JOIN 
    people_city pc ON p.people_id = pc.people_id
LEFT JOIN 
    city c ON pc.city_id = c.city_id
LEFT JOIN 
    people_feeling pf ON p.people_id = pf.people_id
LEFT JOIN 
    feeling f ON pf.feeling_id = f.feeling_id;

SELECT 
    p.name AS person_name,
    g.description AS goal_description
FROM people p
JOIN goal_people gp ON p.people_id = gp.people_id
JOIN goal g ON gp.goal_id = g.goal_id
ORDER BY p.name;