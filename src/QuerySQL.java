public class QuerySQL {

	private String startQuery;
	private String query;

	private QuerySQL(String query) {
		this.startQuery = query;
	}

	public static InsertBuilder insert() {
		return new InsertBuilder("INSERT");
	}

	public static SelectBuilder select(String... cols) {
		return new SelectBuilder(String.format("SELECT %s", parseColumns("*", cols)));
	}

	public static DeleteBuilder delete() {
		return new DeleteBuilder("DELETE");
	}

	public static UpdateBuilder update(String table) {
		return new UpdateBuilder("UPDATE " + table);
	}

	public QuerySQL where(Condition condition) {
		if (condition == null) {
			return this;
		}

		if (query == null || query.isEmpty()) {
			query = "WHERE " + condition.toString();
		} else {
			query += condition.toString();
		}
		return this;
	}

	private static String parseColumns(String emptyValue, String... cols) {
		StringBuilder result = new StringBuilder();

		if (cols == null || cols.length == 0) {
			return emptyValue;
		} else {
			for (String column : cols) {
				result
						.append(", ")
						.append(column);
			}
		}
		return "(" + result.substring(2) + ")";
	}

	private static String parseValues(Object... values) {
		StringBuilder result = new StringBuilder();

		if (values == null || values.length == 0) {
			return "";
		} else {
			for (Object value : values) {
				if (value instanceof String || value instanceof Character) {
					result
							.append(", ")
							.append("'")
							.append(value.toString())
							.append("'");
				} else {
					result
							.append(", ")
							.append(value.toString());
				}
			}
		}
		return "(" + result.substring(2) + ")";
	}

	private static String parseSetters(Condition... setters) {
		StringBuilder result = new StringBuilder();

		if (setters == null || setters.length == 0) {
			return "";
		} else {
			for (Condition setter : setters) {
				result
						.append(", ")
						.append(setter.condition);
			}
		}
		return result.substring(2);
	}

	@Override
	public String toString() {
		if (query == null) {
			return startQuery;
		}

		return String.format("%s %s", startQuery, query);
	}

	public static class Condition {

		private String condition;

		Condition(String condition) {
			this.condition = condition;
		}

		public static Condition and(Condition... conditions) {
			if (conditions == null || conditions.length == 0) {
				return null;
			}

			StringBuilder condition = new StringBuilder();
			int amount = 0;

			for (Condition value : conditions) {
				if (value == null) {
					continue;
				}

				condition
						.append(amount == 0 ? "" : " AND ")
						.append("(")
						.append(value.condition)
						.append(")");

				amount++;
			}

			if (condition.toString().isEmpty()) {
				return null;
			}

			return new Condition(condition.toString());
		}

		public static Condition or(Condition... conditions) {
			if (conditions == null || conditions.length == 0) {
				return null;
			}

			StringBuilder condition = new StringBuilder();
			int amount = 0;

			for (Condition value : conditions) {
				if (value == null) {
					continue;
				}

				condition
						.append(amount == 0 ? "" : " OR ")
						.append("(")
						.append(value.condition)
						.append(")");

				amount++;
			}

			return new Condition(condition.toString());
		}

		public static Condition equals(String field, Object value) {
			return operation("=", field, value);
		}

		public static Condition graterThan(String field, Object value) {
			return operation(">", field, value);
		}

		public static Condition lessThan(String field, Object value) {
			return operation("<", field, value);
		}

		public static Condition graterThanOrEqual(String field, Object value) {
			return operation(">=", field, value);
		}

		public static Condition lessThanOrEqual(String field, Object value) {
			return operation("<=", field, value);
		}

		private static Condition operation(String operation, String field, Object value) {
			if (value == null) {
				return null;
			}

			if (value instanceof String || value instanceof Character) {
				return new Condition(String.format("%s %s '%s'", field, operation, value.toString()));
			}

			return new Condition(String.format("%s %s %s", field, operation, value.toString()));
		}

		@Override
		public String toString() {
			return condition;
		}
	}

	public static class InsertBuilder {

		private String startQuery;

		private InsertBuilder(String startQuery) {
			this.startQuery = startQuery;
		}

		public InsertBuilder into(String table, String... cols) throws InsertQueryException {
			if (cols == null || cols.length == 0) {
				throw new InsertQueryException("You have to set at least one column inside INSERT statement.");
			}

			this.startQuery = String.format("%s INTO %s %s", this.startQuery, table, parseColumns("", cols));
			return this;
		}

		public QuerySQL values(Object... values) {
			return new QuerySQL(String.format("%s VALUES %s", this.startQuery, parseValues(values)));
		}
	}

	public static class SelectBuilder {

		private String startQuery;

		private SelectBuilder(String startQuery) {
			this.startQuery = startQuery;
		}

		public QuerySQL from(String table) {
			return new QuerySQL(String.format("%s FROM %s", this.startQuery, table));
		}
	}

	public static class DeleteBuilder {

		private String startQuery;

		private DeleteBuilder(String startQuery) {
			this.startQuery = startQuery;
		}

		public QuerySQL from(String table) {
			return new QuerySQL(String.format("%s FROM %s", this.startQuery, table));
		}
	}

	public static class UpdateBuilder {

		private String startQuery;

		private UpdateBuilder(String startQuery) {
			this.startQuery = startQuery;
		}

		public QuerySQL set(Condition... setters) {
			return new QuerySQL(String.format("%s SET %s", this.startQuery, parseSetters(setters)));
		}
	}
}