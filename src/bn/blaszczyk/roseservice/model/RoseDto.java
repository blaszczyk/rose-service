package bn.blaszczyk.roseservice.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseservice.tools.TypeManager;

public class RoseDto extends LinkedHashMap<String, String>{
	
	private static final long serialVersionUID = 7851913867785528671L;
	
	private static final Gson GSON = new Gson();
	
	private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
	
	public RoseDto(final Map<?,?> anyMap)
	{
		for(final Map.Entry<?, ?> entry : anyMap.entrySet())
		{
			put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}
	}
	
	public RoseDto(final StringMap<?> stringMap)
	{
		for(final Map.Entry<String, ?> entry : stringMap.entrySet())
		{
			put(entry.getKey(), String.valueOf(entry.getValue()));
		}
	}
	
	public RoseDto(final Readable entity)
	{
		put("type",entity.getEntityName());
		put("id", String.valueOf(entity.getId()));
		for(int i = 0; i < entity.getFieldCount(); i++)
		{
			String stringValue = null;
			final Object field = entity.getFieldValue(i);
			if(field instanceof Date)
				stringValue = DATE_FORMAT.format((Date)field);
			else if(field != null)
				stringValue = field.toString();
			put("f" + i,stringValue);
		}
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			if(entity.getRelationType(i).isSecondMany())
			{
				final List<Integer> ids = new ArrayList<>();
				for(final Readable subEntity : entity.getEntityValueMany(i))
					ids.add(subEntity.getId());
				put("e"+i,GSON.toJson(ids));
			}
			else
			{
				Readable subEntity = entity.getEntityValueOne(i);
				if(subEntity != null)
					put("e"+i,String.valueOf(subEntity.getId()));
			}
		}
	}
	
	public int getId()
	{
		return Integer.parseInt(get("id"));
	}
	
	public Class<?> getType()
	{
		return TypeManager.getClass(get("type"));
	}
	
	public Object getFieldValue(final int index)
	{
		return get("f"+index);
	}
	
	public Integer getEntityId(final int index)
	{
		return Integer.parseInt(get("e"+index));
	}
	
	public List<Integer> getEntityIds(final int index)
	{
		final String idsString = get("e" + index);
		final List<?> ids = GSON.fromJson(idsString, List.class);
		return ids.stream()
				.map(Object::toString)
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}
	
}