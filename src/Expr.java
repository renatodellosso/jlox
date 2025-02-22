import java.util.List;

abstract class Expr {
	interface Visitor<R> {
		R visitTrinaryExpr(Trinary expr);
		R visitBinaryExpr(Binary expr);
		R visitGroupingExpr(Grouping expr);
		R visitLiteralExpr(Literal expr);
		R visitUnaryExpr(Unary expr);
	}

	static class Trinary extends Expr {
		final Expr firstExpr;
		final Token firstOperator;
		final Expr secondExpr;
		final Token secondOperator;
		final Expr thirdExpr;
		Trinary(Expr firstExpr, Token firstOperator, Expr secondExpr, Token secondOperator, Expr thirdExpr) {
			this.firstExpr = firstExpr;
			this.firstOperator = firstOperator;
			this.secondExpr = secondExpr;
			this.secondOperator = secondOperator;
			this.thirdExpr = thirdExpr;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitTrinaryExpr(this);
		}
	}

	static class Binary extends Expr {
		final Expr left;
		final Token operator;
		final Expr right;
		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}
	}

	static class Grouping extends Expr {
		final Expr expression;
		Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}
	}

	static class Literal extends Expr {
		final Object value;
		Literal(Object value) {
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}
	}

	static class Unary extends Expr {
		final Token operator;
		final Expr right;
		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}

	abstract <R> R accept(Visitor<R> visitor);
}
