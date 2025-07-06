import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val FileEarmarkWord: ImageVector
	get() {
		if (_FileEarmarkWord != null) {
			return _FileEarmarkWord!!
		}
		_FileEarmarkWord = ImageVector.Builder(
            name = "FileEarmarkWord",
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
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(5.485f, 6.879f)
				arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, -0.97f, 0.242f)
				lineToRelative(1.5f, 6f)
				arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.967f, 0.01f)
				lineTo(8f, 9.402f)
				lineToRelative(1.018f, 3.73f)
				arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.967f, -0.01f)
				lineToRelative(1.5f, -6f)
				arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.97f, -0.242f)
				lineToRelative(-1.036f, 4.144f)
				lineToRelative(-0.997f, -3.655f)
				arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.964f, 0f)
				lineToRelative(-0.997f, 3.655f)
				lineTo(5.485f, 6.88f)
				close()
			}
			path(
    			fill = SolidColor(Color(0xFF000000)),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(14f, 14f)
				verticalLineTo(4.5f)
				lineTo(9.5f, 0f)
				horizontalLineTo(4f)
				arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, 2f)
				verticalLineToRelative(12f)
				arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 2f)
				horizontalLineToRelative(8f)
				arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, -2f)
				moveTo(9.5f, 3f)
				arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 11f, 4.5f)
				horizontalLineToRelative(2f)
				verticalLineTo(14f)
				arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, 1f)
				horizontalLineTo(4f)
				arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, -1f)
				verticalLineTo(2f)
				arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, -1f)
				horizontalLineToRelative(5.5f)
				close()
			}
		}.build()
		return _FileEarmarkWord!!
	}

private var _FileEarmarkWord: ImageVector? = null
