import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Bookmark_added: ImageVector
	get() {
		if (_Bookmark_added != null) {
			return _Bookmark_added!!
		}
		_Bookmark_added = ImageVector.Builder(
            name = "Bookmark_added",
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
				moveTo(713f, 360f)
				lineTo(600f, 247f)
				lineToRelative(56f, -57f)
				lineToRelative(57f, 57f)
				lineToRelative(141f, -142f)
				lineToRelative(57f, 57f)
				close()
				moveTo(200f, 840f)
				verticalLineToRelative(-640f)
				quadToRelative(0f, -33f, 23.5f, -56.5f)
				reflectiveQuadTo(280f, 120f)
				horizontalLineToRelative(240f)
				verticalLineToRelative(80f)
				horizontalLineTo(280f)
				verticalLineToRelative(518f)
				lineToRelative(200f, -86f)
				lineToRelative(200f, 86f)
				verticalLineToRelative(-278f)
				horizontalLineToRelative(80f)
				verticalLineToRelative(400f)
				lineTo(480f, 720f)
				close()
				moveToRelative(80f, -640f)
				horizontalLineToRelative(240f)
				close()
			}
		}.build()
		return _Bookmark_added!!
	}

private var _Bookmark_added: ImageVector? = null
