class QuerySQLTest extends GroovyTestCase {

	void testInsertExample() {
		QuerySQL query = QuerySQL
				.insert()
				.into("table", "brand", "model")
				.values("Audi", "A4")
				.where(
						QuerySQL.Condition.equals("id", 5)
				)

		assertToString(query, "INSERT INTO table (brand, model) VALUES ('Audi', 'A4') WHERE id = 5")
	}

	void testUpdateExample() {
		QuerySQL query = QuerySQL
				.update("table")
				.set(
						QuerySQL.Condition.equals("brand", "Nissan"),
						QuerySQL.Condition.equals("model", "Micra"),
						QuerySQL.Condition.equals("ready", true),
				)
				.where(
						QuerySQL.Condition.equals("id", 4)
				)

		assertToString(query, "UPDATE table SET brand = 'Nissan', model = 'Micra', ready = true WHERE id = 4")
	}

	void testDeleteExample() {
		QuerySQL query = QuerySQL
				.delete()
				.from("table")
				.where(
						QuerySQL.Condition.and(
								QuerySQL.Condition.or(
										QuerySQL.Condition.equals("id", 4),
										QuerySQL.Condition.equals("id", 5)
								),
								QuerySQL.Condition.graterThan("mileage", 10000)
						)
				)

		assertToString(query, "DELETE FROM table WHERE ((id = 4) OR (id = 5)) AND (mileage > 10000)")
	}

	void testSelectExample() {
		QuerySQL query = QuerySQL
				.select("id", "mileage", "year")
				.from("table")
				.where(
						QuerySQL.Condition.or(
								QuerySQL.Condition.and(
										QuerySQL.Condition.graterThanOrEqual("year", 2010),
										QuerySQL.Condition.lessThanOrEqual("year", 2019)
								),
								QuerySQL.Condition.and(
										QuerySQL.Condition.graterThanOrEqual("year", 2000),
										QuerySQL.Condition.lessThanOrEqual("year", 2009)
								)
						)
				)

		assertToString(query, "SELECT (id, mileage, year) FROM table WHERE ((year >= 2010) AND (year <= 2019)) OR ((year >= 2000) AND (year <= 2009))")
	}
}