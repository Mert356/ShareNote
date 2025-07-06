import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Report: ImageVector
	get() {
		if (_Report != null) {
			return _Report!!
		}
		_Report = ImageVector.Builder(
            name = "Report",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
			path(
    			fill = SolidColor(Color(0xFF000000)),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.EvenOdd
			) {
				moveTo(1.5f, 1f)
				horizontalLineToRelative(13f)
				lineToRelative(0.5f, 0.5f)
				verticalLineToRelative(10f)
				lineToRelative(-0.5f, 0.5f)
				horizontalLineTo(7.707f)
				lineToRelative(-2.853f, 2.854f)
				lineTo(4f, 14.5f)
				verticalLineTo(12f)
				horizontalLineTo(1.5f)
				lineToRelative(-0.5f, -0.5f)
				verticalLineToRelative(-10f)
				lineToRelative(0.5f, -0.5f)
				close()
				moveToRelative(6f, 10f)
				horizontalLineTo(14f)
				verticalLineTo(2f)
				horizontalLineTo(2f)
				verticalLineToRelative(9f)
				horizontalLineToRelative(2.5f)
				lineToRelative(0.5f, 0.5f)
				verticalLineToRelative(1.793f)
				lineToRelative(2.146f, -2.147f)
				lineTo(7.5f, 11f)
				close()
				moveToRelative(0f, -8f)
				horizontalLineToRelative(1f)
				verticalLineToRelative(5f)
				horizontalLineToRelative(-1f)
				verticalLineTo(3f)
				close()
				moveToRelative(0f, 7f)
				horizontalLineToRelative(1f)
				verticalLineTo(9f)
				horizontalLineToRelative(-1f)
				verticalLineToRelative(1f)
				close()
			}
		}.build()
		return _Report!!
	}

private var _Report: ImageVector? = null
