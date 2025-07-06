import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Note_stack: ImageVector
	get() {
		if (_Note_stack != null) {
			return _Note_stack!!
		}
		_Note_stack = ImageVector.Builder(
            name = "Note_stack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
			path(
    			fill = SolidColor(Color.Black),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(280f, 800f)
				verticalLineToRelative(-441f)
				quadToRelative(0f, -33f, 24f, -56f)
				reflectiveQuadToRelative(57f, -23f)
				horizontalLineToRelative(439f)
				quadToRelative(33f, 0f, 56.5f, 23.5f)
				reflectiveQuadTo(880f, 360f)
				verticalLineToRelative(320f)
				lineTo(680f, 880f)
				horizontalLineTo(360f)
				quadToRelative(-33f, 0f, -56.5f, -23.5f)
				reflectiveQuadTo(280f, 800f)
				moveTo(81f, 250f)
				quadToRelative(-6f, -33f, 13f, -59.5f)
				reflectiveQuadToRelative(52f, -32.5f)
				lineToRelative(434f, -77f)
				quadToRelative(33f, -6f, 59.5f, 13f)
				reflectiveQuadToRelative(32.5f, 52f)
				lineToRelative(10f, 54f)
				horizontalLineToRelative(-82f)
				lineToRelative(-7f, -40f)
				lineToRelative(-433f, 77f)
				lineToRelative(40f, 226f)
				verticalLineToRelative(279f)
				quadToRelative(-16f, -9f, -27.5f, -24f)
				reflectiveQuadTo(158f, 684f)
				close()
				moveToRelative(279f, 110f)
				verticalLineToRelative(440f)
				horizontalLineToRelative(280f)
				verticalLineToRelative(-160f)
				horizontalLineToRelative(160f)
				verticalLineToRelative(-280f)
				close()
				moveToRelative(220f, 220f)
			}
		}.build()
		return _Note_stack!!
	}

private var _Note_stack: ImageVector? = null
