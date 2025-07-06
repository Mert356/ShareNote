import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val FileEarmarkPpt: ImageVector
	get() {
		if (_FileEarmarkPpt != null) {
			return _FileEarmarkPpt!!
		}
		_FileEarmarkPpt = ImageVector.Builder(
            name = "FileEarmarkPpt",
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
				moveTo(7f, 5.5f)
				arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 1f)
				verticalLineTo(13f)
				arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 0f)
				verticalLineToRelative(-2f)
				horizontalLineToRelative(1.188f)
				arcToRelative(2.75f, 2.75f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -5.5f)
				close()
				moveTo(8.188f, 10f)
				horizontalLineTo(7f)
				verticalLineTo(6.5f)
				horizontalLineToRelative(1.188f)
				arcToRelative(1.75f, 1.75f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 3.5f)
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
				moveTo(14f, 4.5f)
				verticalLineTo(14f)
				arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
				horizontalLineTo(4f)
				arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
				verticalLineTo(2f)
				arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
				horizontalLineToRelative(5.5f)
				close()
				moveToRelative(-3f, 0f)
				arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.5f, 3f)
				verticalLineTo(1f)
				horizontalLineTo(4f)
				arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 1f)
				verticalLineToRelative(12f)
				arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 1f)
				horizontalLineToRelative(8f)
				arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, -1f)
				verticalLineTo(4.5f)
				close()
			}
		}.build()
		return _FileEarmarkPpt!!
	}

private var _FileEarmarkPpt: ImageVector? = null
